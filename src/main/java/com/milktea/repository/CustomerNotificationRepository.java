package com.milktea.repository;

import com.milktea.entity.CustomerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerNotificationRepository extends JpaRepository<CustomerNotification, Integer> {
    List<CustomerNotification> findByCustomerCustomerIdOrderByCreatedAtDesc(Integer customerId);
    long countByCustomerCustomerIdAndReadFalse(Integer customerId);
}
