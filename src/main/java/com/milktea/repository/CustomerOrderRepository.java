package com.milktea.repository;

import com.milktea.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerOrderRepository
        extends JpaRepository<CustomerOrder,Integer> {

    List<CustomerOrder>
    findByCustomerCustomerIdAndStatus(
            Integer customerId,
            String status
    );
    List<CustomerOrder>
    findByCustomerCustomerId(Integer customerId);

    List<CustomerOrder>
    findByStatusNot(String status);

}