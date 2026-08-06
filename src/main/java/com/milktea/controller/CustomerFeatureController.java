package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
public class CustomerFeatureController {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerFavoriteRepository favoriteRepository;
    private final ProductReviewRepository reviewRepository;
    private final CustomerOrderRepository orderRepository;
    private final CustomerOrderDetailRepository detailRepository;
    private final CustomerNotificationRepository notificationRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;

    public CustomerFeatureController(CustomerRepository customerRepository, ProductRepository productRepository,
                                     CustomerFavoriteRepository favoriteRepository, ProductReviewRepository reviewRepository,
                                     CustomerOrderRepository orderRepository, CustomerOrderDetailRepository detailRepository,
                                     CustomerNotificationRepository notificationRepository, VoucherRepository voucherRepository,
                                     CustomerVoucherRepository customerVoucherRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.detailRepository = detailRepository;
        this.notificationRepository = notificationRepository;
        this.voucherRepository = voucherRepository;
        this.customerVoucherRepository = customerVoucherRepository;
    }

    @GetMapping("/customer/favorites")
    public String favorites(Model model) {
        Customer customer = getCurrentCustomer();
        model.addAttribute("customer", customer);
        model.addAttribute("favorites", favoriteRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customer.getCustomerId()));
        return "customer-favorites";
    }

    @GetMapping("/customer/favorite/toggle/{productId}")
    public String toggleFavorite(@PathVariable Integer productId, @RequestParam(defaultValue = "/customer/menu") String back) {
        Customer customer = getCurrentCustomer();
        favoriteRepository.findByCustomerCustomerIdAndProductProductId(customer.getCustomerId(), productId)
                .ifPresentOrElse(favoriteRepository::delete, () -> productRepository.findById(productId).ifPresent(product -> {
                    CustomerFavorite favorite = new CustomerFavorite();
                    favorite.setCustomer(customer);
                    favorite.setProduct(product);
                    favorite.setCreatedAt(new Date());
                    favoriteRepository.save(favorite);
                }));
        return "redirect:" + safeCustomerPath(back);
    }

    @GetMapping("/customer/product/{id}")
    public String productDetail(@PathVariable Integer id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/customer/menu";
        Customer customer = getCurrentCustomer();
        List<ProductReview> reviews = reviewRepository.findByProductProductIdAndStatusOrderByCreatedAtDesc(id, "VISIBLE");
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0));
        model.addAttribute("reviewCount", reviews.size());
        model.addAttribute("favorite", favoriteRepository.findByCustomerCustomerIdAndProductProductId(customer.getCustomerId(), id).isPresent());
        model.addAttribute("ownReview", reviewRepository.findByCustomerCustomerIdAndProductProductId(customer.getCustomerId(), id).orElse(null));
        model.addAttribute("canReview", hasPurchasedProduct(customer, id));
        return "customer-product-detail";
    }

    @PostMapping("/customer/product/{id}/review")
    public String saveReview(@PathVariable Integer id, @RequestParam Integer rating, @RequestParam String comment,
                             RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer();
        Product product = productRepository.findById(id).orElse(null);
        if (product == null || !hasPurchasedProduct(customer, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn chỉ có thể đánh giá món đã mua và hoàn tất.");
            return "redirect:/customer/product/" + id;
        }
        ProductReview review = reviewRepository.findByCustomerCustomerIdAndProductProductId(customer.getCustomerId(), id)
                .orElseGet(ProductReview::new);
        review.setCustomer(customer);
        review.setProduct(product);
        review.setRating(Math.max(1, Math.min(5, rating)));
        review.setComment(comment.trim());
        review.setCreatedAt(new Date());
        review.setStatus("VISIBLE");
        reviewRepository.save(review);
        redirectAttributes.addFlashAttribute("successMessage", "Đánh giá của bạn đã được lưu.");
        return "redirect:/customer/product/" + id;
    }

    @GetMapping("/customer/notifications")
    public String notifications(Model model) {
        Customer customer = getCurrentCustomer();
        List<CustomerNotification> notifications = notificationRepository.findByCustomerCustomerIdOrderByCreatedAtDesc(customer.getCustomerId());
        notifications.stream().filter(notification -> !notification.isRead()).forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
        model.addAttribute("notifications", notifications);
        return "customer-notifications";
    }

    @GetMapping("/customer/rewards")
    public String rewards(Model model) {
        Customer customer = getCurrentCustomer();
        LocalDate today = LocalDate.now();
        List<Voucher> rewards = voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getRequiredPoints() != null && voucher.getRequiredPoints() > 0)
                .filter(voucher -> voucher.getStartDate() == null || !today.isBefore(voucher.getStartDate()))
                .filter(voucher -> voucher.getEndDate() == null || !today.isAfter(voucher.getEndDate()))
                .toList();
        model.addAttribute("customer", customer);
        model.addAttribute("rewards", rewards);
        model.addAttribute("ownedVouchers", customerVoucherRepository.findByCustomerCustomerIdOrderByRedeemedAtDesc(customer.getCustomerId()));
        return "customer-rewards";
    }

    @GetMapping("/customer/rewards/redeem/{voucherId}")
    public String redeemReward(@PathVariable Integer voucherId, RedirectAttributes redirectAttributes) {
        Customer customer = getCurrentCustomer();
        Voucher voucher = voucherRepository.findById(voucherId).orElse(null);
        int cost = voucher == null || voucher.getRequiredPoints() == null ? 0 : voucher.getRequiredPoints();
        if (voucher == null || cost <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Phần thưởng không còn khả dụng.");
        } else if (customerVoucherRepository.existsByCustomerCustomerIdAndVoucherVoucherId(customer.getCustomerId(), voucherId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã đổi phần thưởng này rồi.");
        } else if ((customer.getPoint() == null ? 0 : customer.getPoint()) < cost) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn chưa đủ điểm để đổi phần thưởng này.");
        } else {
            customer.setPoint(customer.getPoint() - cost);
            customerRepository.save(customer);
            CustomerVoucher owned = new CustomerVoucher();
            owned.setCustomer(customer);
            owned.setVoucher(voucher);
            owned.setRedeemedAt(new Date());
            owned.setStatus("AVAILABLE");
            customerVoucherRepository.save(owned);
            redirectAttributes.addFlashAttribute("successMessage", "Đổi phần thưởng thành công. Mã của bạn: " + voucher.getVoucherCode());
        }
        return "redirect:/customer/rewards";
    }

    private boolean hasPurchasedProduct(Customer customer, Integer productId) {
        return orderRepository.findByCustomerCustomerIdOrderByOrderDateDesc(customer.getCustomerId()).stream()
                .filter(order -> "COMPLETED".equals(order.getStatus()))
                .flatMap(order -> detailRepository.findByCustomerOrderOrderId(order.getOrderId()).stream())
                .anyMatch(detail -> detail.getProduct() != null && productId.equals(detail.getProduct().getProductId()));
    }

    private Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            List<Customer> matches = customerRepository.findByFullNameContaining(auth.getName());
            if (!matches.isEmpty()) return matches.get(0);
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

    private String safeCustomerPath(String back) {
        return back != null && back.startsWith("/customer/") ? back : "/customer/menu";
    }
}
