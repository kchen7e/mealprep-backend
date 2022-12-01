package com.mealprep.MealPrep.entities.meals;


import com.mealprep.MealPrep.entities.api.recipe.RecipeDTO;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;

@Data
@NoArgsConstructor
public class Breakfast {
    ArrayList<RecipeDTO> selectedRecipes;
}
