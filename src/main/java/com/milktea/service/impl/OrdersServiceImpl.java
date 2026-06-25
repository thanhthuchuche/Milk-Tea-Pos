package com.milktea.service.impl;

import com.milktea.entity.Orders;
import com.milktea.repository.OrdersRepository;
import com.milktea.service.OrdersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersServiceImpl(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Override
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    @Override
    public Orders getOrderById(Integer id) {
        return ordersRepository.findById(id).orElse(null);
    }

    @Override
    public Orders saveOrder(Orders order) {
        return ordersRepository.save(order);
    }

    @Override
    public void deleteOrder(Integer id) {
        ordersRepository.deleteOrderDetailsByOrderId(id);
        ordersRepository.deleteById(id);
    }
    @Override
    public List<Orders> searchOrders(
            String keyword){

        return ordersRepository
                .findByCustomerFullNameContainingIgnoreCase(
                        keyword);

    }
}