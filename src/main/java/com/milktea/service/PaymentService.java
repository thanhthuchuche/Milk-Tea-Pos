package com.milktea.service;

import com.milktea.entity.Payment;

import java.util.List;

public interface PaymentService {

    List<Payment> getAllPayments();

    Payment getPaymentById(Integer id);

    void savePayment(Payment payment);

    void deletePayment(Integer id);
}