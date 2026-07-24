package com.milktea.controller;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.Date;
import java.util.List;

@Controller
public class CustomerCartController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderDetailRepository customerOrderDetailRepository;

    public CustomerCartController(
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderDetailRepository customerOrderDetailRepository) {

        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderDetailRepository = customerOrderDetailRepository;
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
        double total = 0.0;
        for (CustomerOrderDetail d : details) {
            total += d.getSubtotal();
        }
        order.setTotalAmount(total);
        customerOrderRepository.save(order);
    }

    @GetMapping("/customer-cart/add/{customerId}/{productId}")
    public String addToCart(
            @PathVariable Integer customerId,
            @PathVariable Integer productId) {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            customer = getCurrentCustomer();
        }

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
        }

        return "customer-cart";
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
        }

        return "redirect:/customer/orders";
    }

    @GetMapping("/customer/orders")
    public String myOrders(Model model) {
        Customer customer = getCurrentCustomer();

        List<CustomerOrder> orders = customerOrderRepository.findByCustomerCustomerId(customer.getCustomerId());
        model.addAttribute("orders", orders);

        return "my-orders";
    }

    @GetMapping("/my-orders/{id}")
    public String orderDetail(@PathVariable Integer id, Model model) {
        CustomerOrder order = customerOrderRepository.findById(id).orElse(null);
        if (order == null) {
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
        if (order != null) {
            List<CustomerOrderDetail> details = customerOrderDetailRepository.findByCustomerOrderOrderId(id);
            customerOrderDetailRepository.deleteAll(details);
            customerOrderRepository.delete(order);
        }

        return "redirect:/customer/orders";
    }
}