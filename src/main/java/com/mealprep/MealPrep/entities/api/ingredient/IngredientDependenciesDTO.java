package com.mealprep.MealPrep.entities.api.ingredient;

import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class IngredientDependenciesDTO extends IngredientDTO {

    @Getter
    @Setter
    Set<Ingredient> ingredients;

//    public IngredientDependenciesAPI(@NotNull String ingredientName) {
//        super(ingredientName);
//    }

    public IngredientDependenciesDTO(@NotNull String ingredientName,
                                     Set<Ingredient> ingredients) {
        super(ingredientName);
        this.ingredients = ingredients;
    }
}
