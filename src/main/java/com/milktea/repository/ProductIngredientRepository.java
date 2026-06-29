package com.milktea.repository;

import com.milktea.entity.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductIngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    List<ProductIngredient> findByProductProductId(Integer productId);
}