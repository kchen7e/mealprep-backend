package com.mealprep.MealPrep.entities.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealprep.MealPrep.measures.Unit;
import lombok.*;

import javax.persistence.*;

@Embeddable
@NoArgsConstructor
public final class RecipeIngredient {
    @JoinColumn(name = "ingredient", referencedColumnName = "ingredient_name")
    @Getter
    private String ingredientName;

    @Getter
    @Setter
    private Unit unit;


    @Getter
    private String displayName;


    public RecipeIngredient(@NonNull String ingredientName) {
        displayName = ingredientName;
        this.ingredientName = ingredientName.toLowerCase();
        this.unit = new Unit();
    }

    public RecipeIngredient(@NonNull String ingredientName, Unit unit) {
        displayName = ingredientName;
        this.ingredientName = ingredientName.toLowerCase();
        this.unit = unit;
    }

    public void setIngredientName(String ingredientName) {
        displayName = ingredientName;
        this.ingredientName = ingredientName.toLowerCase();
    }


    @Override
    public int hashCode() {
        return ingredientName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecipeIngredient) {
            return ingredientName.equals(((RecipeIngredient)obj).getIngredientName());
        } else {
            return super.equals(obj);
        }
    }

}