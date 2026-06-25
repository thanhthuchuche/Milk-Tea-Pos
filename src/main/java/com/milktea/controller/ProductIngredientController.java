package com.milktea.controller;

import com.milktea.entity.ProductIngredient;
import com.milktea.service.IngredientService;
import com.milktea.service.ProductIngredientService;
import com.milktea.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductIngredientController {

    private final ProductIngredientService productIngredientService;
    private final ProductService productService;
    private final IngredientService ingredientService;

    public ProductIngredientController(
            ProductIngredientService productIngredientService,
            ProductService productService,
            IngredientService ingredientService) {

        this.productIngredientService = productIngredientService;
        this.productService = productService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/product-ingredients")
    public String getAll(Model model) {

        model.addAttribute(
                "productIngredients",
                productIngredientService.getAllProductIngredients()
        );

        return "productingredient-list";
    }

    @GetMapping("/product-ingredients/add")
    public String add(Model model) {

        model.addAttribute(
                "productIngredient",
                new ProductIngredient()
        );

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        model.addAttribute(
                "ingredients",
                ingredientService.getAllIngredients()
        );

        return "productingredient-form";
    }

    @PostMapping("/product-ingredients/save")
    public String save(
            ProductIngredient productIngredient) {

        productIngredientService
                .saveProductIngredient(productIngredient);

        return "redirect:/product-ingredients";
    }

    @GetMapping("/product-ingredients/delete/{id}")
    public String delete(
            @PathVariable Integer id) {

        productIngredientService.deleteProductIngredient(id);

        return "redirect:/product-ingredients";
    }
}