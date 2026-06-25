package com.milktea.repository;

import com.milktea.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    long countByStatus(String status);
    List<Orders>
    findByCustomerFullNameContainingIgnoreCase(
            String keyword);

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