package com.milktea.repository;

import com.milktea.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository
        extends JpaRepository<Payment, Integer> {

    Payment findByInvoice_InvoiceId(Integer invoiceId);
}