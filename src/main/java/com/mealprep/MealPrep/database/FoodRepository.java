package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.market.Food;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends CrudRepository<Food, String> {

  @Query(
      value =
          "SELECT f.* FROM food f "
              + "JOIN food_ingredient fi ON f.food_name = fi.food_name "
              + "WHERE fi.ingredient_name = :#{#ingredient.ingredientName}",
      nativeQuery = true)
  Set<Food> getFoodCandidate(Ingredient ingredient);
}
