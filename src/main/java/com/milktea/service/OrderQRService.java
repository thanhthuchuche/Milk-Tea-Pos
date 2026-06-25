package com.milktea.service;

import com.milktea.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderQRService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderQRService(
            OrderDetailRepository orderDetailRepository) {

        this.orderDetailRepository = orderDetailRepository;
    }

    public Integer getQuantity(
            Integer tableId,
            Integer productId) {

        Integer quantity =
                orderDetailRepository
                        .getQuantityByTableAndProduct(
                                tableId,
                                productId);

        return quantity == null ? 0 : quantity;
    }

}