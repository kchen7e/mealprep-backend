package com.mealprep.MealPrep.entities.api.recipe;

import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public final class RecipeIngredientsDTO extends RecipeDTO {

  @Setter @Getter public Set<RecipeIngredient> ingredients;

  //    public RecipeIngredientsAPI(@NonNull String recipeName) {
  //        super(recipeName);
  //    }

  public RecipeIngredientsDTO(@NonNull String recipeName, Set<RecipeIngredient> ingredients) {
    super(recipeName);
    this.ingredients = ingredients;
  }
}
