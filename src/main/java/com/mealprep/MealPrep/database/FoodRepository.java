package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.market.Food;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.util.HashSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends CrudRepository<Food, String> {

  @Query(
      value = "SELECT f FROM food WHERE :ingredient MEMBER OF f" + ".forIngredients",
      nativeQuery = true)
  HashSet<Food> getFoodCandidate(Ingredient ingredient);
}
