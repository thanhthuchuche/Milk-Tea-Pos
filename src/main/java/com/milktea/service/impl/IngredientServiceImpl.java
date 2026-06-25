package com.milktea.service.impl;

import com.milktea.entity.Ingredient;
import com.milktea.repository.IngredientRepository;
import com.milktea.service.IngredientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(
            IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Override
    public Ingredient getIngredientById(Integer id) {
        return ingredientRepository.findById(id).orElse(null);
    }

    @Override
    public Ingredient saveIngredient(
            Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Override
    public void deleteIngredient(Integer id) {
        ingredientRepository.deleteById(id);
    }
}