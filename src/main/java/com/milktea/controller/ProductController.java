package com.milktea.controller;

import com.milktea.entity.Product;
import com.milktea.service.CategoryService;
import com.milktea.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService,
                             CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/products")
    public String getAllProducts(Model model) {

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "product-list";
    }

    @GetMapping("/products/add")
    public String addProduct(Model model) {

        model.addAttribute(
                "product",
                new Product()
        );

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "product-form";
    }
    @PostMapping("/products/save")
    public String saveProduct(
            Product product,
            @RequestParam Integer categoryId,
            @RequestParam("imageFile") MultipartFile imageFile) {

        try {

            if (!imageFile.isEmpty()) {

                String fileName =
                        imageFile.getOriginalFilename();

                String uploadDir =
                        "src/main/resources/static/images/";

                java.nio.file.Path path =
                        java.nio.file.Paths.get(
                                uploadDir + fileName
                        );

                java.nio.file.Files.write(
                        path,
                        imageFile.getBytes()
                );

                product.setImage(fileName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        product.setCategory(
                categoryService.getCategoryById(categoryId)
        );

        productService.saveProduct(product);

        return "redirect:/products";
    }
    @GetMapping("/products/edit/{id}")
    public String editProduct(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "product",
                productService.getProductById(id)
        );

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "product-form";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(
            @PathVariable Integer id) {

        productService.deleteProduct(id);

        return "redirect:/products";
    }

    @GetMapping("/products/search")
    public String searchProduct(
            @RequestParam("keyword") String keyword,
            Model model) {

        model.addAttribute(
                "products",
                productService.searchProducts(keyword)
        );

        return "product-list";
    }

}