package com.milktea.controller;

import com.milktea.entity.Category;
import com.milktea.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public String getAllCategories(Model model) {

        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );

        return "category-list";
    }

    @GetMapping("/categories/add")
    public String addCategory(Model model) {

        model.addAttribute(
                "category",
                new Category()
        );

        return "category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category) {

        categoryService.saveCategory(category);

        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(
            @PathVariable Integer id,
            Model model) {

        Category category =
                categoryService.getCategoryById(id);

        model.addAttribute(
                "category",
                category
        );

        return "category-form";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(
            @PathVariable Integer id) {

        categoryService.deleteCategory(id);

        return "redirect:/categories";
    }
}