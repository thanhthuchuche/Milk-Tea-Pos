package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.List;
import java.time.LocalDate;

@Controller
public class CustomerCartController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderDetailRepository customerOrderDetailRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;

    public CustomerCartController(
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderDetailRepository customerOrderDetailRepository,
            VoucherRepository voucherRepository,
            CustomerVoucherRepository customerVoucherRepository) {

        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderDetailRepository = customerOrderDetailRepository;
        this.voucherRepository = voucherRepository;
        this.customerVoucherRepository = customerVoucherRepository;
    }

    private Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            List<Customer> matches = customerRepository.findByFullNameContaining(username);
            if (!matches.isEmpty()) {
                return matches.get(0);
            }
        }
        
        List<Customer> all = customerRepository.findAll();
        if (!all.isEmpty()) {
            return all.get(0);
        }
        
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Khách Hàng");
        newCustomer.setPhone("0900000000");
        newCustomer.setEmail("customer@codaoquan.com");
        newCustomer.setPoint(0);
        return customerRepository.save(newCustomer);
    }

    private void recalculateCartTotal(CustomerOrder order) {
        if (order == null) return;
        List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(order.getOrderId());
        double originalAmount = 0.0;
        for (CustomerOrderDetail d : details) {
            originalAmount += d.getSubtotal();
        }
        double discountAmount = 0.0;
        if (order.getVoucher() != null && order.getVoucher().getDiscountPercent() != null) {
            discountAmount = originalAmount * order.getVoucher().getDiscountPercent() / 100.0;
        }
        order.setOriginalAmount(originalAmount);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(Math.max(0.0, originalAmount - discountAmount));
        customerOrderRepository.save(order);
    }

    @GetMapping("/customer-cart/add/{customerId}/{productId}")
    public String addToCart(
            @PathVariable Integer customerId,
            @PathVariable Integer productId) {

        Customer customer = getCurrentCustomer();

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return "redirect:/customer/menu";
        }

        CustomerOrder order;
        List<CustomerOrder> carts = customerOrderRepository.findByCustomerCustomerIdAndStatus(customer.getCustomerId(), "CART");

        if (carts.isEmpty()) {
            order = new CustomerOrder();
            order.setCustomer(customer);
            order.setOrderDate(new Date());
            order.setStatus("CART");
            order.setTotalAmount(0.0);
            order = customerOrderRepository.save(order);
        } else {
            order = carts.get(0);
        }

        CustomerOrderDetail detail = null;
        List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(order.getOrderId());

        for (CustomerOrderDetail d : details) {
            if (d.getProduct() != null && d.getProduct().getProductId().equals(productId)) {
                detail = d;
                break;
            }
        }

        if (detail != null) {
            detail.setQuantity(detail.getQuantity() + 1);
            detail.setSubtotal(detail.getQuantity() * detail.getPrice());
        } else {
            detail = new CustomerOrderDetail();
            detail.setCustomerOrder(order);
            detail.setProduct(product);
            detail.setQuantity(1);
            detail.setPrice(product.getPrice());
            detail.setSubtotal(product.getPrice());
        }

        customerOrderDetailRepository.save(detail);
        recalculateCartTotal(order);

        String productName = URLEncoder.encode(product.getProductName(), StandardCharsets.UTF_8);
        return "redirect:/customer/menu?success=" + productName;
    }

    @GetMapping("/customer-cart")
    public String customerCart(Model model) {
        Customer customer = getCurrentCustomer();

        List<CustomerOrder> carts = customerOrderRepository.findByCustomerCustomerIdAndStatus(customer.getCustomerId(), "CART");

        if (!carts.isEmpty()) {
            CustomerOrder order = carts.get(0);
            recalculateCartTotal(order);

            List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(order.getOrderId());
            model.addAttribute("order", order);
            model.addAttribute("details", details);
            model.addAttribute("appliedVoucher", order.getVoucher());
            model.addAttribute("originalAmount", order.getOriginalAmount());
            model.addAttribute("discountAmount", order.getDiscountAmount());
        }

        return "customer-cart";
    }

    @PostMapping("/customer-cart/apply-voucher")
    public String applyVoucher(@RequestParam String voucherCode, RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer();
        CustomerOrder cart = getCart(customer);
        if (cart == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng đang trống.");
            return "redirect:/customer-cart";
        }

        String normalizedCode = voucherCode == null ? "" : voucherCode.trim();
        Voucher voucher = voucherRepository.findAll().stream()
                .filter(item -> item.getVoucherCode() != null && item.getVoucherCode().equalsIgnoreCase(normalizedCode))
                .findFirst().orElse(null);
        LocalDate today = LocalDate.now();
        boolean active = voucher != null
                && (voucher.getStartDate() == null || !today.isBefore(voucher.getStartDate()))
                && (voucher.getEndDate() == null || !today.isAfter(voucher.getEndDate()));
        boolean owned = voucher != null && customerVoucherRepository
                .existsByCustomerCustomerIdAndVoucherVoucherCodeIgnoreCaseAndStatus(customer.getCustomerId(), normalizedCode, "AVAILABLE");

        if (!active) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã voucher không tồn tại hoặc đã hết hạn.");
        } else if (!owned) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã này chưa được đổi trong tài khoản của bạn.");
        } else {
            cart.setVoucher(voucher);
            recalculateCartTotal(cart);
            redirectAttributes.addFlashAttribute("successMessage", "Đã áp dụng mã " + voucher.getVoucherCode() + " giảm " + voucher.getDiscountPercent() + "%.");
        }
        return "redirect:/customer-cart";
    }

    @GetMapping("/customer-cart/remove-voucher")
    public String removeVoucher(RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer();
        CustomerOrder cart = getCart(customer);
        if (cart != null) {
            cart.setVoucher(null);
            recalculateCartTotal(cart);
            redirectAttributes.addFlashAttribute("successMessage", "Đã bỏ mã voucher khỏi giỏ hàng.");
        }
        return "redirect:/customer-cart";
    }

    @GetMapping("/customer-cart/increase/{id}")
    public String increaseQuantity(@PathVariable Integer id) {
        CustomerOrderDetail detail = customerOrderDetailRepository.findById(id).orElse(null);

        if (detail != null) {
            detail.setQuantity(detail.getQuantity() + 1);
            detail.setSubtotal(detail.getQuantity() * detail.getPrice());
            customerOrderDetailRepository.save(detail);

            recalculateCartTotal(detail.getCustomerOrder());
        }

        return "redirect:/customer-cart";
    }

    @GetMapping("/customer-cart/decrease/{id}")
    public String decreaseQuantity(@PathVariable Integer id) {
        CustomerOrderDetail detail = customerOrderDetailRepository.findById(id).orElse(null);

        if (detail != null) {
            CustomerOrder order = detail.getCustomerOrder();
            if (detail.getQuantity() > 1) {
                detail.setQuantity(detail.getQuantity() - 1);
                detail.setSubtotal(detail.getQuantity() * detail.getPrice());
                customerOrderDetailRepository.save(detail);
            } else {
                customerOrderDetailRepository.delete(detail);
            }
            recalculateCartTotal(order);
        }

        return "redirect:/customer-cart";
    }

    @GetMapping("/customer-order/checkout")
    public String checkout() {
        Customer customer = getCurrentCustomer();

        List<CustomerOrder> carts = customerOrderRepository.findByCustomerCustomerIdAndStatus(customer.getCustomerId(), "CART");

        if (!carts.isEmpty()) {
            CustomerOrder order = carts.get(0);
            recalculateCartTotal(order);
            order.setOrderDate(new Date());
            order.setStatus("PENDING");
            customerOrderRepository.save(order);
            if (order.getVoucher() != null) {
                customerVoucherRepository.findByCustomerCustomerIdAndVoucherVoucherId(
                                customer.getCustomerId(), order.getVoucher().getVoucherId())
                        .ifPresent(customerVoucher -> {
                            customerVoucher.setStatus("USED");
                            customerVoucherRepository.save(customerVoucher);
                        });
            }
        }

        return "redirect:/customer/orders";
    }

    private CustomerOrder getCart(Customer customer) {
        List<CustomerOrder> carts = customerOrderRepository
                .findByCustomerCustomerIdAndStatus(customer.getCustomerId(), "CART");
        return carts.isEmpty() ? null : carts.get(0);
    }

    @GetMapping("/customer/orders")
    public String myOrders(Model model) {
        Customer customer = getCurrentCustomer();

        List<CustomerOrder> orders = customerOrderRepository
                .findByCustomerCustomerIdOrderByOrderDateDesc(customer.getCustomerId())
                .stream()
                .filter(order -> !"CART".equals(order.getStatus()))
                .toList();
        model.addAttribute("orders", orders);

        return "my-orders";
    }

    @GetMapping("/my-orders/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        CustomerOrder order = customerOrderRepository.findById(id).orElse(null);
        if (order == null || !belongsToCurrentCustomer(order)) {
            return "redirect:/customer/orders";
        }

        List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "customer-order-detail";
    }

    @GetMapping("/customer-order/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        CustomerOrder order = customerOrderRepository.findById(id).orElse(null);
        if (order != null && belongsToCurrentCustomer(order)) {
            List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(id);
            customerOrderDetailRepository.deleteAll(details);
            customerOrderRepository.delete(order);
        }

        return "redirect:/customer/orders";
    }

    @GetMapping("/customer-order/reorder/{id}")
    public String reorder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        CustomerOrder sourceOrder = customerOrderRepository.findById(id).orElse(null);
        if (sourceOrder == null || !belongsToCurrentCustomer(sourceOrder)) {
            return "redirect:/customer/orders";
        }

        Customer customer = getCurrentCustomer();
        CustomerOrder cart = getOrCreateCart(customer);
        List<CustomerOrderDetail> cartDetails = customerOrderDetailRepository
                .findByCustomerOrderOrderId(cart.getOrderId());

        for (CustomerOrderDetail sourceDetail : customerOrderDetailRepository
                .findByCustomerOrderOrderId(id)) {
            if (sourceDetail.getProduct() == null) {
                continue;
            }

            CustomerOrderDetail targetDetail = cartDetails.stream()
                    .filter(detail -> detail.getProduct() != null
                            && detail.getProduct().getProductId().equals(sourceDetail.getProduct().getProductId()))
                    .findFirst()
                    .orElse(null);

            if (targetDetail == null) {
                targetDetail = new CustomerOrderDetail();
                targetDetail.setCustomerOrder(cart);
                targetDetail.setProduct(sourceDetail.getProduct());
                targetDetail.setPrice(sourceDetail.getPrice());
                targetDetail.setQuantity(sourceDetail.getQuantity());
            } else {
                targetDetail.setQuantity(targetDetail.getQuantity() + sourceDetail.getQuantity());
            }
            targetDetail.setSubtotal(targetDetail.getQuantity() * targetDetail.getPrice());
            customerOrderDetailRepository.save(targetDetail);
            cartDetails.add(targetDetail);
        }

        recalculateCartTotal(cart);
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm các món từ đơn cũ vào giỏ hàng.");
        return "redirect:/customer-cart";
    }

    private CustomerOrder getOrCreateCart(Customer customer) {
        List<CustomerOrder> carts = customerOrderRepository
                .findByCustomerCustomerIdAndStatus(customer.getCustomerId(), "CART");
        if (!carts.isEmpty()) {
            return carts.get(0);
        }

        CustomerOrder cart = new CustomerOrder();
        cart.setCustomer(customer);
        cart.setOrderDate(new Date());
        cart.setStatus("CART");
        cart.setTotalAmount(0.0);
        return customerOrderRepository.save(cart);
    }

    private boolean belongsToCurrentCustomer(CustomerOrder order) {
        Customer current = getCurrentCustomer();
        return order.getCustomer() != null
                && current.getCustomerId().equals(order.getCustomer().getCustomerId());
    }
}
