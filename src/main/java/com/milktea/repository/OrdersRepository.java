package com.milktea.repository;

import com.milktea.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    long countByStatus(String status);
    @Query("""
           SELECT o
           FROM Orders o
           LEFT JOIN o.customer c
           LEFT JOIN o.tableCafe t
           WHERE LOWER(COALESCE(c.fullName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(COALESCE(t.tableNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
           ORDER BY o.orderId DESC
           """)
    List<Orders> searchOrders(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM order_detail WHERE order_id = ?1", nativeQuery = true)
    void deleteOrderDetailsByOrderId(Integer orderId);

    @Query("""
       SELECT o
       FROM Orders o
       WHERE o.tableCafe.tableId = :tableId
       AND o.status='PENDING'
       ORDER BY o.orderId DESC
       """)
    List<Orders> findPendingOrderByTable(Integer tableId);
}
