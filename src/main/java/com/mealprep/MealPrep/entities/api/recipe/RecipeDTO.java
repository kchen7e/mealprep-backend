package com.mealprep.MealPrep.entities.api.recipe;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class RecipeDTO {
  @Getter private String recipeName;

  @Getter private String displayName;

  public RecipeDTO(@NonNull String recipeName) {
    this.displayName = recipeName;
    this.recipeName = recipeName.toLowerCase();
  }

  public void setRecipeName(@NonNull String recipeName) {
    this.displayName = recipeName;
    this.recipeName = recipeName.toLowerCase();
  }
}
