package com.milktea.service;

import com.milktea.entity.ProductIngredient;
import java.util.List;

public interface ProductIngredientService {

    List<ProductIngredient> getAllProductIngredients();

    ProductIngredient getProductIngredientById(Integer id);

    ProductIngredient saveProductIngredient(ProductIngredient productIngredient);

    void deleteProductIngredient(Integer id);
}