package com.milktea.repository;

import com.milktea.entity.CustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Integer> {
    List<CustomerVoucher> findByCustomerCustomerIdOrderByRedeemedAtDesc(Integer customerId);
    boolean existsByCustomerCustomerIdAndVoucherVoucherId(Integer customerId, Integer voucherId);
}
