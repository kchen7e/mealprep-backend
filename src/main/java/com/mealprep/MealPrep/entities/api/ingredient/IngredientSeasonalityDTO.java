package com.mealprep.MealPrep.entities.api.ingredient;

import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class IngredientSeasonalityDTO extends IngredientDTO {

  @Getter @Setter public Set<Ingredient.Season> seasonality;

  //    public IngredientSeasonalityAPI(@NonNull String ingredientName) {
  //        super(ingredientName);
  //    }

  public IngredientSeasonalityDTO(
      @NonNull String ingredientName, Set<Ingredient.Season> seasonality) {
    super(ingredientName);
    this.seasonality = seasonality;
  }
}
