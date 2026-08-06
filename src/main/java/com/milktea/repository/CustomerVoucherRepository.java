package com.milktea.repository;

import com.milktea.entity.CustomerVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerVoucherRepository extends JpaRepository<CustomerVoucher, Integer> {
    List<CustomerVoucher> findByCustomerCustomerIdOrderByRedeemedAtDesc(Integer customerId);
    boolean existsByCustomerCustomerIdAndVoucherVoucherId(Integer customerId, Integer voucherId);
    boolean existsByCustomerCustomerIdAndVoucherVoucherCodeIgnoreCaseAndStatus(Integer customerId, String voucherCode, String status);
    Optional<CustomerVoucher> findByCustomerCustomerIdAndVoucherVoucherId(Integer customerId, Integer voucherId);
}
