package com.milktea.service.impl;

import com.milktea.entity.Invoice;
import com.milktea.repository.InvoiceRepository;
import com.milktea.repository.PaymentRepository;
import com.milktea.service.InvoiceService;
import org.springframework.stereotype.Service;
import com.milktea.entity.Payment;

import java.util.List;

@Service
public class InvoiceServiceImpl
        implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public InvoiceServiceImpl(
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository) {

        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }
    @Override
    public Double getTotalRevenue() {

        Double revenue = invoiceRepository.getTotalRevenue();

        return revenue == null ? 0 : revenue;
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id)
                .orElse(null);
    }

    @Override
    public void saveInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    @Override
    public void deleteInvoice(Integer id) {

        Payment payment =
                paymentRepository.findByInvoice_InvoiceId(id);

        if (payment != null) {
            paymentRepository.delete(payment);
        }

        invoiceRepository.deleteById(id);
    }
}