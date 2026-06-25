package com.milktea.controller;

import com.milktea.entity.OrderDetail;
import com.milktea.entity.Orders;
import com.milktea.entity.Product;
import com.milktea.service.OrderDetailService;
import com.milktea.service.OrdersService;
import com.milktea.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    private final OrdersService ordersService;
    private final ProductService productService;

    public OrderDetailController(
            OrderDetailService orderDetailService,
            OrdersService ordersService,
            ProductService productService) {

        this.orderDetailService = orderDetailService;
        this.ordersService = ordersService;
        this.productService = productService;
    }

    @GetMapping("/orderdetails")
    public String getAllOrderDetails(Model model) {

        model.addAttribute(
                "orderDetails",
                orderDetailService.getAllOrderDetails()
        );

        return "orderdetail-list";
    }

    @GetMapping("/orderdetails/add")
    public String addOrderDetail(Model model) {

        model.addAttribute(
                "orderDetail",
                new OrderDetail()
        );

        model.addAttribute(
                "orders",
                ordersService.getAllOrders()
        );

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "orderdetail-form";
    }

    @PostMapping("/orderdetails/save")
    public String saveOrderDetail(
            @ModelAttribute("orderDetail") OrderDetail orderDetail) {

        if (orderDetail.getOrders() != null
                && orderDetail.getOrders().getOrderId() != null) {

            Orders orders = ordersService.getOrderById(
                    orderDetail.getOrders().getOrderId()
            );

            orderDetail.setOrders(orders);
        }

        if (orderDetail.getProduct() != null
                && orderDetail.getProduct().getProductId() != null) {

            Product product = productService.getProductById(
                    orderDetail.getProduct().getProductId()
            );

            orderDetail.setProduct(product);
        }

        orderDetailService.saveOrderDetail(orderDetail);

        return "redirect:/orderdetails";
    }

    @GetMapping("/orderdetails/edit/{id}")
    public String editOrderDetail(
            @PathVariable Integer id,
            Model model) {

        model.addAttribute(
                "orderDetail",
                orderDetailService.getOrderDetailById(id)
        );

        model.addAttribute(
                "orders",
                ordersService.getAllOrders()
        );

        model.addAttribute(
                "products",
                productService.getAllProducts()
        );

        return "orderdetail-form";
    }

    @GetMapping("/orderdetails/delete/{id}")
    public String deleteOrderDetail(
            @PathVariable Integer id) {

        orderDetailService.deleteOrderDetail(id);

        return "redirect:/orderdetails";
    }
}