package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.recipe.RecipeDTO;
import com.mealprep.MealPrep.entities.calendar.Week;
import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;
import com.mealprep.MealPrep.measures.Unit;
import com.mealprep.MealPrep.service.RecipeService;
import java.util.*;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingListController {
  private final RecipeService recipeService;

  public ShoppingListController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @PostMapping(
      value = "/get",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Map<String, Unit> getIngredientList(@RequestBody Week week) {
    Map<String, Unit> ingredientUnits = new HashMap<>();

    week.getWeek()
        .forEach(
            day -> {
              Stream.of(day.getBreakfast(), day.getLunch(), day.getDinner())
                  .flatMap(List::stream)
                  .forEach(recipeDTO -> processRecipe(recipeDTO, ingredientUnits));
            });

    return ingredientUnits;
  }

  private void processRecipe(RecipeDTO recipeDTO, Map<String, Unit> ingredientUnits) {
    recipeService
        .getRecipeByName(recipeDTO.getRecipeName())
        .ifPresent(
            recipe ->
                recipe
                    .getIngredients()
                    .forEach(
                        recipeIngredient -> addIngredientUnit(recipeIngredient, ingredientUnits)));
  }

  private void addIngredientUnit(
      RecipeIngredient recipeIngredient, Map<String, Unit> ingredientUnits) {
    String ingredientName = recipeIngredient.getIngredientName();
    Unit unit = recipeIngredient.getUnit();

    ingredientUnits.merge(ingredientName, unit, Unit::combine);
  }
}
