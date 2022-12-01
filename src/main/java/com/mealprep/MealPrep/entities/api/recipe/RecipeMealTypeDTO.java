package com.mealprep.MealPrep.entities.api.recipe;


import com.mealprep.MealPrep.entities.recipe.Recipe;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public final class RecipeMealTypeDTO extends RecipeDTO {
    @Getter
    @Setter
    public Set<Recipe.MealType> mealType;

//    public RecipeMealTypeAPI(@NotNull String recipeName) {
//        super(recipeName);
//    }

    public RecipeMealTypeDTO(@NotNull String recipeName,
                             Set<Recipe.MealType> mealType) {
        super(recipeName);
        this.mealType = mealType;
    }
}
