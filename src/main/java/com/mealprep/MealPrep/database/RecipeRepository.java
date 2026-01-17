package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.recipe.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, String> {}
