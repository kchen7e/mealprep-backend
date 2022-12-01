package com.mealprep.MealPrep.entities.api.ingredient;


import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IngredientDTO {

    @Getter
    private String ingredientName;

    @Getter
    private String displayName;

    public IngredientDTO(@NotNull String ingredientName) {
        this.displayName = ingredientName;
        this.ingredientName = ingredientName.toLowerCase();
    }

    public void setIngredientName(@NotNull String ingredientName) {
        this.displayName = ingredientName;
        this.ingredientName = ingredientName.toLowerCase();
    }

}
