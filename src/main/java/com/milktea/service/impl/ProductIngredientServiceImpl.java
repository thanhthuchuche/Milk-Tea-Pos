package com.milktea.service.impl;

import com.milktea.entity.ProductIngredient;
import com.milktea.repository.ProductIngredientRepository;
import com.milktea.service.ProductIngredientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductIngredientServiceImpl
        implements ProductIngredientService {

    private final ProductIngredientRepository repository;

    public ProductIngredientServiceImpl(
            ProductIngredientRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ProductIngredient> getAllProductIngredients() {
        return repository.findAll();
    }

    @Override
    public ProductIngredient getProductIngredientById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public ProductIngredient saveProductIngredient(
            ProductIngredient productIngredient) {
        return repository.save(productIngredient);
    }

    @Override
    public void deleteProductIngredient(Integer id) {
        repository.deleteById(id);
    }
}