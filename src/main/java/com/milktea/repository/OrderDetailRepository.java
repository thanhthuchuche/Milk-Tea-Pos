package com.milktea.repository;

import com.milktea.dto.ProductSalesDTO;
import com.milktea.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface OrderDetailRepository
        extends JpaRepository<OrderDetail,Integer> {
    List<OrderDetail> findByOrdersOrderId(
            Integer orderId);

    @Query("""
            SELECT new com.milktea.dto.ProductSalesDTO(
                    od.product.productName,
                    SUM(od.quantity)
            )
            FROM OrderDetail od
            GROUP BY od.product.productName
            ORDER BY SUM(od.quantity) DESC
            """)
    List<ProductSalesDTO> getTopSellingProducts();


    @Query("""
       SELECT od
       FROM OrderDetail od
       WHERE od.orders.orderId=:orderId
       AND od.product.productId=:productId
       """)
    Optional<OrderDetail> findProductInOrder(
            Integer orderId,
            Integer productId);


    @Query("""
       SELECT od
       FROM OrderDetail od
       WHERE od.orders.orderId=:orderId
       """)
    List<OrderDetail> findAllByOrderId(Integer orderId);

    @Query("""
SELECT COALESCE(SUM(od.quantity),0)
FROM OrderDetail od
WHERE od.orders.tableCafe.tableId=:tableId
AND od.orders.status='PENDING'
AND od.product.productId=:productId
""")
    Integer getQuantityByTableAndProduct(
            Integer tableId,
            Integer productId);

}