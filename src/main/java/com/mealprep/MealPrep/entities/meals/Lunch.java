package com.mealprep.MealPrep.entities.meals;

import com.mealprep.MealPrep.entities.recipe.Recipe;
import java.io.Serializable;
import java.util.ArrayList;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Lunch implements Serializable {
  ArrayList<Recipe> selectedRecipes;
}
