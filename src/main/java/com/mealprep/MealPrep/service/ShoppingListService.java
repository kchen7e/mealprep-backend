package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.entities.market.Food;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
class ShoppingListService {

  public Set<Food> convertIngredientsToFood(List<Ingredient> ingredientList) {
    Set<Food> shoppingList = new HashSet<>();
    ingredientList.forEach(
        ingredient -> {
          if (ingredient.getIngredientName() == null) {}
        });
    return shoppingList;
  }
}
