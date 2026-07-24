package com.milktea.controller;

import com.milktea.entity.Category;
import com.milktea.entity.Customer;
import com.milktea.entity.Product;
import com.milktea.service.CategoryService;
import com.milktea.service.CustomerService;
import com.milktea.service.ProductService;
import com.milktea.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customer-menu")
public class CustomerMenuController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    public CustomerMenuController(
            ProductService productService,
            CategoryService categoryService,
            CustomerService customerService,
            CustomerRepository customerRepository) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    // Explicit Customer Menu route by customerId
    @GetMapping("/{customerId}")
    public String showCustomerMenu(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String success,
            Model model) {

        Customer customer = customerService.getCustomerById(customerId);

        if (customer == null) {
            customer = getAuthenticatedCustomer();
        }

        List<Category> categories = categoryService.getAllCategories();
        List<Product> products = productService.getAllProducts();

        model.addAttribute("customer", customer);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("successMessage", success);

        return "customer-menu";
    }

    private Customer getAuthenticatedCustomer() {
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
        
        Customer defaultCust = new Customer();
        defaultCust.setFullName("Khách Hàng");
        defaultCust.setPhone("0900000000");
        defaultCust.setEmail("customer@codaoquan.com");
        defaultCust.setPoint(0);
        return customerRepository.save(defaultCust);
    }
}