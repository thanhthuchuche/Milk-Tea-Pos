package com.milktea.repository;

import com.milktea.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface InvoiceRepository
        extends JpaRepository<Invoice,Integer> {

    @Query("""
            SELECT SUM(i.totalAmount)
            FROM Invoice i
            """)
    Double getTotalRevenue();

    default List<Double> getRevenueData() {
        return findAllByOrderByInvoiceDateAsc().stream()
                .map(Invoice::getTotalAmount)
                .filter(Objects::nonNull)
                .toList();
    }

    default List<String> getRevenueLabels() {
        return findAllByOrderByInvoiceDateAsc().stream()
                .map(Invoice::getInvoiceDate)
                .filter(Objects::nonNull)
                .map(this::formatDate)
                .toList();
    }

    default List<Integer> getRevenueMonths() {
        Map<Integer, Double> monthlyTotals = new LinkedHashMap<>();
        for (Invoice invoice : findAllByOrderByInvoiceDateAsc()) {
            if (invoice.getInvoiceDate() == null) {
                continue;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(invoice.getInvoiceDate());
            int month = calendar.get(Calendar.MONTH) + 1;
            monthlyTotals.merge(month, invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount(), Double::sum);
        }
        return new ArrayList<>(monthlyTotals.keySet());
    }

    default List<Double> getRevenueByMonth() {
        Map<Integer, Double> monthlyTotals = new LinkedHashMap<>();
        for (Invoice invoice : findAllByOrderByInvoiceDateAsc()) {
            if (invoice.getInvoiceDate() == null) {
                continue;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(invoice.getInvoiceDate());
            int month = calendar.get(Calendar.MONTH) + 1;
            monthlyTotals.merge(month, invoice.getTotalAmount() == null ? 0.0 : invoice.getTotalAmount(), Double::sum);
        }
        return new ArrayList<>(monthlyTotals.values());
    }

    List<Invoice> findAllByOrderByInvoiceDateAsc();

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM");
        return formatter.format(date);
    }
}