package com.milktea.service;

import com.milktea.entity.ProductIngredient;
import java.util.List;

public interface ProductIngredientService {

    List<ProductIngredient> getAllProductIngredients();

    List<ProductIngredient> getProductIngredientsByProductId(Integer productId);

    ProductIngredient getProductIngredientById(Integer id);

    ProductIngredient saveProductIngredient(ProductIngredient productIngredient);

    void deleteProductIngredient(Integer id);
}
