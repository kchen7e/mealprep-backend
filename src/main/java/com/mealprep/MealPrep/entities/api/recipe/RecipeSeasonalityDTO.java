package com.mealprep.MealPrep.entities.api.recipe;

import com.mealprep.MealPrep.entities.recipe.Recipe;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public final class RecipeSeasonalityDTO extends RecipeDTO {
  @Getter @Setter public Set<Recipe.Season> seasonality;

  //    public RecipeSeasonalityAPI(@NonNull String recipeName) {
  //        super(recipeName);
  //    }

  public RecipeSeasonalityDTO(@NonNull String recipeName, Set<Recipe.Season> seasonality) {
    super(recipeName);
    this.seasonality = seasonality;
  }
}
