package com.mealprep.MealPrep.entities.meals;


import com.mealprep.MealPrep.entities.recipe.Recipe;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Lunch implements Serializable {
    ArrayList<Recipe> selectedRecipes;
}