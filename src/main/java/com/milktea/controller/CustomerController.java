package com.milktea.controller;

import com.milktea.entity.Customer;
import com.milktea.repository.CustomerRepository;
import com.milktea.service.CategoryService;
import com.milktea.service.CustomerService;
import com.milktea.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CustomerRepository customerRepository;

    public CustomerController(
            CustomerService customerService,
            ProductService productService,
            CategoryService categoryService,
            CustomerRepository customerRepository) {

        this.customerService = customerService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/customers")
    public String getAllCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customer-list";
    }

    @GetMapping("/customers/add")
    public String addCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer) {
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable Integer id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        return "customer-form";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }

    @GetMapping("/customers/search")
    public String searchCustomer(@RequestParam String keyword, Model model) {
        model.addAttribute("customers", customerService.searchCustomers(keyword));
        return "customer-list";
    }

    @GetMapping("/customer/menu")
    public String customerMenu(Model model) {
        Customer customer = getAuthenticatedCustomer();
        model.addAttribute("customer", customer);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
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