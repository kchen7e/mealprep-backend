package com.mealprep.MealPrep.entities.api.recipe;


import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;



@NoArgsConstructor
public class RecipeDTO {
    @Getter
    private String recipeName;

    @Getter
    private String displayName;

    public RecipeDTO(@NotNull String recipeName) {
        this.displayName = recipeName;
        this.recipeName = recipeName.toLowerCase();
    }

    public void setRecipeName(@NotNull String recipeName) {
        this.displayName = recipeName;
        this.recipeName = recipeName.toLowerCase();
    }




}
