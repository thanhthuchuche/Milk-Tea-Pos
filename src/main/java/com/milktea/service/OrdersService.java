package com.milktea.service;

import com.milktea.entity.Orders;
import java.util.List;

public interface OrdersService {

    List<Orders> getAllOrders();

    Orders getOrderById(Integer id);

    Orders saveOrder(Orders order);

    void deleteOrder(Integer id);

    List<Orders> searchOrders(
            String keyword);
}