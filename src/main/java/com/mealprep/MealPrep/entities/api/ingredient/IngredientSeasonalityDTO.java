package com.mealprep.MealPrep.entities.api.ingredient;

import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class IngredientSeasonalityDTO extends IngredientDTO {

    @Getter
    @Setter
    public Set<Ingredient.Season> seasonality;


//    public IngredientSeasonalityAPI(@NotNull String ingredientName) {
//        super(ingredientName);
//    }

    public IngredientSeasonalityDTO(@NotNull String ingredientName,
                                    Set<Ingredient.Season> seasonality) {
        super(ingredientName);
        this.seasonality = seasonality;
    }


}
