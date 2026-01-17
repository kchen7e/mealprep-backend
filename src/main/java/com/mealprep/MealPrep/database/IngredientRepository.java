package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.recipe.Ingredient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, String> {}
