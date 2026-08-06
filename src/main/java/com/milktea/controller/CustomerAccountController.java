package com.milktea.controller;

import com.milktea.entity.Customer;
import com.milktea.entity.CustomerOrder;
import com.milktea.repository.CustomerOrderDetailRepository;
import com.milktea.repository.CustomerOrderRepository;
import com.milktea.repository.CustomerRepository;
import com.milktea.repository.VoucherRepository;
import com.milktea.repository.CustomerNotificationRepository;
import com.milktea.repository.CustomerFavoriteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CustomerAccountController {

    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderDetailRepository customerOrderDetailRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerNotificationRepository notificationRepository;
    private final CustomerFavoriteRepository favoriteRepository;

    public CustomerAccountController(
            CustomerRepository customerRepository,
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderDetailRepository customerOrderDetailRepository,
            VoucherRepository voucherRepository,
            CustomerNotificationRepository notificationRepository,
            CustomerFavoriteRepository favoriteRepository) {
        this.customerRepository = customerRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderDetailRepository = customerOrderDetailRepository;
        this.voucherRepository = voucherRepository;
        this.notificationRepository = notificationRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @GetMapping("/customer/dashboard")
    public String dashboard(Model model) {
        Customer customer = getCurrentCustomer();
        List<CustomerOrder> allOrders = customerOrderRepository
                .findByCustomerCustomerIdOrderByOrderDateDesc(customer.getCustomerId());
        List<CustomerOrder> submittedOrders = allOrders.stream()
                .filter(order -> !"CART".equals(order.getStatus()))
                .toList();

        CustomerOrder cart = allOrders.stream()
                .filter(order -> "CART".equals(order.getStatus()))
                .findFirst()
                .orElse(null);

        int cartItemCount = cart == null ? 0 : customerOrderDetailRepository
                .findByCustomerOrderOrderId(cart.getOrderId())
                .stream()
                .mapToInt(detail -> detail.getQuantity() == null ? 0 : detail.getQuantity())
                .sum();

        long pendingOrders = submittedOrders.stream()
                .filter(order -> "PENDING".equals(order.getStatus()))
                .count();
        long completedOrders = submittedOrders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .count();
        double totalSpent = submittedOrders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .map(CustomerOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        LocalDate today = LocalDate.now();
        model.addAttribute("customer", customer);
        model.addAttribute("recentOrders", submittedOrders.stream().limit(5).toList());
        model.addAttribute("cartItemCount", cartItemCount);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("completedOrders", completedOrders);
        model.addAttribute("totalSpent", totalSpent);
        model.addAttribute("favoriteCount", favoriteRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customer.getCustomerId()).size());
        model.addAttribute("unreadNotifications", notificationRepository.countByCustomerCustomerIdAndReadFalse(customer.getCustomerId()));
        model.addAttribute("vouchers", voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getStartDate() == null || !today.isBefore(voucher.getStartDate()))
                .filter(voucher -> voucher.getEndDate() == null || !today.isAfter(voucher.getEndDate()))
                .toList());
        return "customer-dashboard";
    }

    @GetMapping("/customer/account")
    public String account(Model model) {
        model.addAttribute("customer", getCurrentCustomer());
        return "customer-account";
    }

    @PostMapping("/customer/account/save")
    public String saveAccount(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer();
        customer.setFullName(fullName.trim());
        customer.setPhone(phone.trim());
        customer.setEmail(email.trim());
        customerRepository.save(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Thông tin tài khoản đã được cập nhật.");
        return "redirect:/customer/account";
    }

    private Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            List<Customer> matches = customerRepository.findByFullNameContaining(auth.getName());
            if (!matches.isEmpty()) {
                return matches.get(0);
            }
        }

        return customerRepository.findAll().stream().findFirst().orElseGet(() -> {
            Customer customer = new Customer();
            customer.setFullName("Khách Hàng");
            customer.setPhone("0900000000");
            customer.setEmail("customer@codaoquan.com");
            customer.setPoint(0);
            return customerRepository.save(customer);
        });
    }
}
