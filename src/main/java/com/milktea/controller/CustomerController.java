package com.milktea.controller;

import com.milktea.entity.Customer;
import com.milktea.service.CategoryService;
import com.milktea.service.CustomerService;
import com.milktea.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final CategoryService categoryService;
    private final ProductService productService;

    public CustomerController(
            CustomerService customerService,
            ProductService productService,
            CategoryService categoryService) {

        this.customerService = customerService;
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/customers")
    public String getAllCustomers(Model model) {

        model.addAttribute(
                "customers",
                customerService.getAllCustomers()
        );

        return "customer-list";
    }

    @GetMapping("/customers/add")
    public String addCustomer(Model model) {

        model.addAttribute(
                "customer",
                new Customer()
        );

        return "customer-form";
    }

    @PostMapping("/customers/save")
    public String saveCustomer(Customer customer) {

        customerService.saveCustomer(customer);

        return "redirect:/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "customer",
                customerService.getCustomerById(id)
        );

        return "customer-form";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(
            @PathVariable Integer id) {

        customerService.deleteCustomer(id);

        return "redirect:/customers";
    }

    @GetMapping("/customers/search")
    public String searchCustomer(
            @RequestParam String keyword,
            Model model) {

        model.addAttribute(
                "customers",
                customerService.searchCustomers(keyword)
        );

        return "customer-list";
    }
    @GetMapping("/customer/menu")
    public String customerMenu(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "customer-menu";

    }
}