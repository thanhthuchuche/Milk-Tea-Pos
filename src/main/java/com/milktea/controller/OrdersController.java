package com.milktea.controller;

import com.milktea.entity.Orders;
import com.milktea.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrdersController {

    private final OrdersService ordersService;
    private final CustomerService customerService;
    private final TableCafeService tableCafeService;
    private final VoucherService voucherService;
    private final UserService userService;
    private final OrderDetailService orderDetailService;

    public OrdersController(
            OrdersService ordersService,
            CustomerService customerService,
            TableCafeService tableCafeService,
            VoucherService voucherService,
            UserService userService,
            OrderDetailService orderDetailService) {

        this.ordersService = ordersService;
        this.customerService = customerService;
        this.tableCafeService = tableCafeService;
        this.voucherService = voucherService;
        this.userService = userService;
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {

        model.addAttribute("orders", ordersService.getAllOrders());

        return "order-list";
    }

    @GetMapping("/orders/add")
    public String addOrder(Model model) {

        model.addAttribute("order", new Orders());

        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("tables", tableCafeService.getAllTables());
        model.addAttribute("vouchers", voucherService.getAllVouchers());
        model.addAttribute("users", userService.getAllUsers());

        return "order-form";
    }

    @PostMapping("/orders/save")
    public String saveOrder(
            Orders order,
            @RequestParam Integer customer,
            @RequestParam Integer tableCafe,
            @RequestParam(required = false) Integer voucher,
            @RequestParam(required = false) Integer user
    ) {

        order.setCustomer(customerService.getCustomerById(customer));
        order.setTableCafe(tableCafeService.getTableById(tableCafe));

        if (voucher != null) {
            com.milktea.entity.Voucher v = voucherService.getVoucherById(voucher);
            order.setVoucher(v);
            
            java.time.LocalDate today = java.time.LocalDate.now();
            boolean isActive = true;
            if (v.getStartDate() != null && today.isBefore(v.getStartDate())) {
                isActive = false;
            }
            if (v.getEndDate() != null && today.isAfter(v.getEndDate())) {
                isActive = false;
            }
            
            if (isActive && v.getDiscountPercent() != null) {
                double discount = v.getDiscountPercent() / 100.0;
                double originalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : 0.0;
                order.setTotalAmount(originalAmount * (1.0 - discount));
            }
        } else {
            order.setVoucher(null);
        }
        if (user != null) {
            order.setUser(userService.getUserById(user));
        } else {
            order.setUser(null);
        }

        ordersService.saveOrder(order);

        return "redirect:/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {

        ordersService.deleteOrder(id);

        return "redirect:/orders";
    }
    @GetMapping("/orders/detail/{id}")
    public String orderDetail(
            @PathVariable Integer id,
            Model model){

        model.addAttribute(
                "orderDetails",
                orderDetailService.getByOrderId(id));

        model.addAttribute(
                "orderId",
                id);

        return "order-detail-list";
    }
    @GetMapping("/orders/search")
    public String searchOrder(
            @RequestParam String keyword,
            Model model){

        model.addAttribute(
                "orders",
                ordersService.searchOrders(keyword));

        return "order-list";
    }
}