package com.milktea.service.impl;

import com.milktea.entity.Orders;
import com.milktea.entity.Invoice;
import com.milktea.entity.Payment;
import com.milktea.repository.InvoiceRepository;
import com.milktea.repository.OrdersRepository;
import com.milktea.repository.PaymentRepository;
import com.milktea.service.OrdersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public OrdersServiceImpl(
            OrdersRepository ordersRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository) {
        this.ordersRepository = ordersRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
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
    @Transactional
    public void deleteOrder(Integer id) {
        Invoice invoice = invoiceRepository.findByOrders_OrderId(id);
        if (invoice != null) {
            Payment payment = paymentRepository.findByInvoice_InvoiceId(invoice.getInvoiceId());
            if (payment != null) {
                paymentRepository.delete(payment);
            }
            invoiceRepository.delete(invoice);
        }
        ordersRepository.deleteOrderDetailsByOrderId(id);
        ordersRepository.deleteById(id);
    }
    @Override
    public List<Orders> searchOrders(
            String keyword){

        return ordersRepository.searchOrders(keyword == null ? "" : keyword.trim());

    }
}
