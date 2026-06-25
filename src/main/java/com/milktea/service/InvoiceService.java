package com.milktea.service;

import com.milktea.entity.Invoice;

import java.util.List;

public interface InvoiceService {

    List<Invoice> getAllInvoices();

    Invoice getInvoiceById(Integer id);

    void saveInvoice(Invoice invoice);

    void deleteInvoice(Integer id);

    Double getTotalRevenue();
}