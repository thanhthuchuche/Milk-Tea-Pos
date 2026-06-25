package com.milktea.controller;


import com.milktea.service.CategoryService;
import com.milktea.service.OrderQRService;
import com.milktea.service.ProductService;
import com.milktea.service.TableCafeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MenuController {

    private final ProductService productService;
    private final TableCafeService tableCafeService;
    private final OrderQRService orderQRService;
    private final CategoryService categoryService;

    public MenuController(
            ProductService productService,
            TableCafeService tableCafeService,
            OrderQRService orderQRService,
            CategoryService categoryService) {

        this.productService = productService;
        this.tableCafeService = tableCafeService;
        this.orderQRService = orderQRService;
        this.categoryService = categoryService;
    }

    @GetMapping("/menu/{tableId}")
    public String menu(
            @PathVariable Integer tableId,
            @RequestParam(required = false)
            String success,
            Model model){

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        model.addAttribute(
                "table",
                tableCafeService.getTableById(tableId)
        );

        model.addAttribute(
                "orderQRService",
                orderQRService
        );
        model.addAttribute(
                "categories",
                categoryService.getAllCategories()
        );
        model.addAttribute(
                "success",
                success);

        return "qr-menu";
    }

}