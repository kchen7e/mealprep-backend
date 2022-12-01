package com.mealprep.MealPrep.entities.api.recipe;

import com.mealprep.MealPrep.entities.recipe.Recipe;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;



public final class RecipeSeasonalityDTO extends RecipeDTO {
    @Getter
    @Setter
    public Set<Recipe.Season> seasonality;

//    public RecipeSeasonalityAPI(@NotNull String recipeName) {
//        super(recipeName);
//    }

    public RecipeSeasonalityDTO(@NotNull String recipeName,
                                Set<Recipe.Season> seasonality) {
        super(recipeName);
        this.seasonality = seasonality;
    }
}
