package com.milktea.controller;

import com.milktea.entity.Category;
import com.milktea.entity.Customer;
import com.milktea.entity.Product;
import com.milktea.service.CategoryService;
import com.milktea.service.CustomerService;
import com.milktea.service.ProductService;
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

    public CustomerMenuController(
            ProductService productService,
            CategoryService categoryService,
            CustomerService customerService) {

        this.productService = productService;
        this.categoryService = categoryService;
        this.customerService = customerService;
    }

    // Hiển thị menu cho khách hàng
    @GetMapping("/{customerId}")
    public String showCustomerMenu(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String success,
            Model model) {

        Customer customer =
                customerService.getCustomerById(customerId);

        if (customer == null) {
            return "redirect:/customers";
        }

        List<Category> categories =
                categoryService.getAllCategories();

        List<Product> products =
                productService.getAllProducts();

        model.addAttribute(
                "customer",
                customer
        );

        model.addAttribute(
                "categories",
                categories
        );

        model.addAttribute(
                "products",
                products
        );

        model.addAttribute(
                "successMessage",
                success
        );

        return "customer-menu";
    }

}