package com.milktea.controller;

import com.milktea.entity.OrderDetail;
import com.milktea.entity.Orders;
import com.milktea.repository.OrderDetailRepository;
import com.milktea.repository.OrdersRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TableOrderController {

    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;

    public TableOrderController(
            OrdersRepository ordersRepository,
            OrderDetailRepository orderDetailRepository) {

        this.ordersRepository = ordersRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    @GetMapping("/table-order/{tableId}")
    public String tableOrder(
            @PathVariable Integer tableId,
            Model model) {

        List<Orders> orders =
                ordersRepository.findPendingOrderByTable(tableId);

        if (orders.isEmpty()) {

            model.addAttribute("message",
                    "Bàn này chưa có đơn hàng");

            model.addAttribute("order", null);

            model.addAttribute("details", List.of());

            return "table-order";
        }

        Orders order = orders.get(0);

        List<OrderDetail> details =
                orderDetailRepository.findAllByOrderId(
                        order.getOrderId());

        model.addAttribute("order", order);

        model.addAttribute("details", details);

        return "table-order";
    }
    @GetMapping("/edit-note/{detailId}")
    public String editNote(
            @PathVariable Integer detailId,
            Model model) {

        OrderDetail detail =
                orderDetailRepository.findById(detailId)
                        .orElse(null);

        model.addAttribute(
                "detail",
                detail);

        return "edit-note";
    }
    @PostMapping("/save-note")
    public String saveNote(
            @RequestParam Integer detailId,
            @RequestParam String note) {

        OrderDetail detail =
                orderDetailRepository.findById(detailId)
                        .orElse(null);

        detail.setNote(note);

        orderDetailRepository.save(detail);

        Integer orderId =
                detail.getOrders()
                        .getOrderId();

        return "redirect:/table-order/" + orderId;
    }
}