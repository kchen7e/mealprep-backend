package com.mealprep.MealPrep.entities.api.recipe;

import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


public final class RecipeIngredientsDTO extends RecipeDTO {

    @Setter
    @Getter
    public Set<RecipeIngredient> ingredients;

//    public RecipeIngredientsAPI(@NotNull String recipeName) {
//        super(recipeName);
//    }

    public RecipeIngredientsDTO(@NotNull String recipeName,
                                Set<RecipeIngredient> ingredients) {
        super(recipeName);
        this.ingredients = ingredients;
    }
}
