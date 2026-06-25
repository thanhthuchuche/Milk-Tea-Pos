package com.milktea.controller;

import com.milktea.entity.InventoryTransaction;
import com.milktea.service.IngredientService;
import com.milktea.service.InventoryTransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class InventoryTransactionController {

    private final InventoryTransactionService transactionService;
    private final IngredientService ingredientService;

    public InventoryTransactionController(
            InventoryTransactionService transactionService,
            IngredientService ingredientService) {

        this.transactionService = transactionService;
        this.ingredientService = ingredientService;
    }

    @GetMapping("/transactions")
    public String getAllTransactions(Model model) {

        model.addAttribute(
                "transactions",
                transactionService.getAllTransactions()
        );

        return "transaction-list";
    }

    @GetMapping("/transactions/add")
    public String addTransaction(Model model) {

        model.addAttribute(
                "transaction",
                new InventoryTransaction()
        );

        model.addAttribute(
                "ingredients",
                ingredientService.getAllIngredients()
        );

        return "transaction-form";
    }

    @PostMapping("/transactions/save")
    public String saveTransaction(
            @ModelAttribute InventoryTransaction transaction) {

        transactionService.saveTransaction(transaction);

        return "redirect:/transactions";
    }

    @GetMapping("/transactions/edit/{id}")
    public String editTransaction(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "transaction",
                transactionService.getTransactionById(id)
        );

        model.addAttribute(
                "ingredients",
                ingredientService.getAllIngredients()
        );

        return "transaction-form";
    }

    @GetMapping("/transactions/delete/{id}")
    public String deleteTransaction(
            @PathVariable Integer id) {

        transactionService.deleteTransaction(id);

        return "redirect:/transactions";
    }
}