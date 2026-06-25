package com.milktea.controller;

import com.milktea.entity.Ingredient;
import com.milktea.service.IngredientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(
            IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredients")
    public String getAllIngredients(Model model) {

        model.addAttribute(
                "ingredients",
                ingredientService.getAllIngredients()
        );

        return "ingredient-list";
    }

    @GetMapping("/ingredients/add")
    public String addIngredient(Model model) {

        model.addAttribute(
                "ingredient",
                new Ingredient()
        );

        return "ingredient-form";
    }

    @PostMapping("/ingredients/save")
    public String saveIngredient(
            Ingredient ingredient) {

        ingredientService.saveIngredient(ingredient);

        return "redirect:/ingredients";
    }

    @GetMapping("/ingredients/edit/{id}")
    public String editIngredient(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "ingredient",
                ingredientService.getIngredientById(id)
        );

        return "ingredient-form";
    }

    @GetMapping("/ingredients/delete/{id}")
    public String deleteIngredient(
            @PathVariable Integer id) {

        ingredientService.deleteIngredient(id);

        return "redirect:/ingredients";
    }
}