package com.milktea.repository;

import com.milktea.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository
        extends JpaRepository<Invoice,Integer> {

    @Query("""
            SELECT SUM(i.totalAmount)
            FROM Invoice i
            """)
    Double getTotalRevenue();

    @Query("""
       SELECT i.totalAmount
       FROM Invoice i
       ORDER BY i.invoiceDate
       """)
    List<Double> getRevenueData();

    @Query(value = """
       SELECT DATE_FORMAT(invoice_date,'%d/%m')
       FROM invoice
       ORDER BY invoice_date
       """, nativeQuery = true)
    List<String> getRevenueLabels();

    @Query(value = """
SELECT MONTH(invoice_date)
FROM invoice
GROUP BY MONTH(invoice_date)
ORDER BY MONTH(invoice_date)
""", nativeQuery = true)
    List<Integer> getRevenueMonths();

    @Query(value = """
SELECT SUM(total_amount)
FROM invoice
GROUP BY MONTH(invoice_date)
ORDER BY MONTH(invoice_date)
""", nativeQuery = true)
    List<Double> getRevenueByMonth();

}