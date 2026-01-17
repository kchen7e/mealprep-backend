package com.mealprep.MealPrep.entities.api.ingredient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class IngredientDTO {

  @Getter private String ingredientName;

  @Getter private String displayName;

  public IngredientDTO(@NonNull String ingredientName) {
    this.displayName = ingredientName;
    this.ingredientName = ingredientName.toLowerCase();
  }

  public void setIngredientName(@NonNull String ingredientName) {
    this.displayName = ingredientName;
    this.ingredientName = ingredientName.toLowerCase();
  }
}
