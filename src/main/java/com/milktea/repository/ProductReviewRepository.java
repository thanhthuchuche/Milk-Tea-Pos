package com.milktea.repository;

import com.milktea.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    List<ProductReview> findByProductProductIdAndStatusOrderByCreatedAtDesc(Integer productId, String status);
    List<ProductReview> findByStatusOrderByCreatedAtDesc(String status);
    Optional<ProductReview> findByCustomerCustomerIdAndProductProductId(Integer customerId, Integer productId);
}
