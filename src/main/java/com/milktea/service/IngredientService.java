package com.milktea.service;

import com.milktea.entity.Ingredient;
import java.util.List;

public interface IngredientService {

    List<Ingredient> getAllIngredients();

    Ingredient getIngredientById(Integer id);

    Ingredient saveIngredient(Ingredient ingredient);

    void deleteIngredient(Integer id);
}