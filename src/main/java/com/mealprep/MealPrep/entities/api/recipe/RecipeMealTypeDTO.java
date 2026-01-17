package com.mealprep.MealPrep.entities.api.recipe;

import com.mealprep.MealPrep.entities.recipe.Recipe;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public final class RecipeMealTypeDTO extends RecipeDTO {
  @Getter @Setter public Set<Recipe.MealType> mealType;

  //    public RecipeMealTypeAPI(@NonNull String recipeName) {
  //        super(recipeName);
  //    }

  public RecipeMealTypeDTO(@NonNull String recipeName, Set<Recipe.MealType> mealType) {
    super(recipeName);
    this.mealType = mealType;
  }
}
