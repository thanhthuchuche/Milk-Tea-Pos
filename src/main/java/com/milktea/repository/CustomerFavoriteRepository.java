package com.milktea.repository;

import com.milktea.entity.CustomerFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerFavoriteRepository extends JpaRepository<CustomerFavorite, Integer> {
    List<CustomerFavorite> findByCustomerCustomerIdOrderByCreatedAtDesc(Integer customerId);
    Optional<CustomerFavorite> findByCustomerCustomerIdAndProductProductId(Integer customerId, Integer productId);
}
