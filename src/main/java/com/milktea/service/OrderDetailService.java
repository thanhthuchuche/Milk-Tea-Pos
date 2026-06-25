package com.milktea.service;

import com.milktea.entity.OrderDetail;
import java.util.List;

public interface OrderDetailService {

    List<OrderDetail> getAllOrderDetails();

    OrderDetail getOrderDetailById(Integer id);

    OrderDetail saveOrderDetail(OrderDetail orderDetail);

    void deleteOrderDetail(Integer id);

    List<OrderDetail> getByOrderId(
            Integer orderId);
}