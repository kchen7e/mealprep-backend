package com.mealprep.MealPrep.entities.meals;

import com.mealprep.MealPrep.entities.api.recipe.RecipeDTO;
import java.util.ArrayList;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Breakfast {
  ArrayList<RecipeDTO> selectedRecipes;
}
