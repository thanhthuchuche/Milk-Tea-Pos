package com.milktea.controller;

import com.milktea.entity.Orders;
import com.milktea.repository.OrdersRepository;
import com.milktea.repository.TableCafeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TablePaymentController {

    private final OrdersRepository ordersRepository;
    private final TableCafeRepository tableCafeRepository;

    public TablePaymentController(
            OrdersRepository ordersRepository,
            TableCafeRepository tableCafeRepository) {

        this.ordersRepository = ordersRepository;
        this.tableCafeRepository = tableCafeRepository;
    }

    @GetMapping("/table-payment/{orderId}")
    public String payment(
            @PathVariable Integer orderId) {

        Orders order =
                ordersRepository.findById(orderId)
                        .orElse(null);

        if (order == null) {

            return "redirect:/tables";

        }

        order.setStatus("PAID");

        ordersRepository.save(order);

        tableCafeRepository.updatePaid(
                order.getTableCafe().getTableId()
        );

        return "redirect:/tables?success";
    }
}