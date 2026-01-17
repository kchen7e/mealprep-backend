package com.mealprep.MealPrep.entities.api.ingredient;

import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class IngredientDependenciesDTO extends IngredientDTO {

  @Getter @Setter Set<Ingredient> ingredients;

  //    public IngredientDependenciesAPI(@NonNull String ingredientName) {
  //        super(ingredientName);
  //    }

  public IngredientDependenciesDTO(@NonNull String ingredientName, Set<Ingredient> ingredients) {
    super(ingredientName);
    this.ingredients = ingredients;
  }
}
