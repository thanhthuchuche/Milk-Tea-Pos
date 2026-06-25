package com.milktea.repository;


import com.milktea.entity.CustomerOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderDetailRepository
        extends JpaRepository<CustomerOrderDetail,Integer> {

    List<CustomerOrderDetail>
    findByCustomerOrderOrderId(Integer orderId);
    List<CustomerOrderDetail>
    findByCustomerOrderCustomerCustomerId(Integer customerId);

}