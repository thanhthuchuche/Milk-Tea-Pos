package com.milktea.controller;

import com.milktea.entity.TableCafe;
import com.milktea.service.TableCafeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TableCafeController {

    private final TableCafeService tableCafeService;

    public TableCafeController(
            TableCafeService tableCafeService) {
        this.tableCafeService = tableCafeService;
    }

    @GetMapping("/tables")
    public String getAllTables(Model model) {

        model.addAttribute(
                "tables",
                tableCafeService.getAllTables()
        );

        return "table-list";
    }

    @GetMapping("/tables/add")
    public String addTable(Model model) {

        model.addAttribute(
                "table",
                new TableCafe()
        );

        return "table-form";
    }

    @PostMapping("/tables/save")
    public String saveTable(TableCafe table) {

        if (table.getStatus() == null || table.getStatus().isEmpty()) {
            table.setStatus("AVAILABLE");
        }

        tableCafeService.saveTable(table);

        return "redirect:/tables";
    }

    @GetMapping("/tables/edit/{id}")
    public String editTable(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "table",
                tableCafeService.getTableById(id)
        );

        return "table-form";
    }

    @GetMapping("/tables/delete/{id}")
    public String deleteTable(
            @PathVariable Integer id) {

        tableCafeService.deleteTable(id);

        return "redirect:/tables";
    }
}