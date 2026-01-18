package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.calendar.Week;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.mealprep.MealPrep.entities.recipe.Recipe;
import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;
import com.mealprep.MealPrep.service.IngredientService;
import com.mealprep.MealPrep.service.RecipeService;
import java.util.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingListController {
  private final RecipeService recipeService;
  private final IngredientService ingredientService;

  public ShoppingListController(RecipeService recipeService, IngredientService ingredientService) {
    this.recipeService = recipeService;
    this.ingredientService = ingredientService;
  }

  @PostMapping(
      value = "/get",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Map<String, Integer> getIngredientList(@RequestBody Week week) {
    Map<Recipe, Integer> recipeRecords = new HashMap<>();
    Map<Ingredient, Integer> ingredientsRecord = new HashMap<>();
    Map<String, Integer> ingredientsRecord1 = new HashMap<>();
    Map<RecipeIngredient, Integer> ingredientsRecord2 = new HashMap<>();
    List<Recipe> recipes = new ArrayList<>();
    week.getWeek()
        .forEach(
            day -> {
              var breakfast = day.getBreakfast();
              breakfast.forEach(
                  recipeDTO -> {
                    var recipe = recipeService.getRecipeByName(recipeDTO.getRecipeName());
                    if (recipe.isPresent()) {
                      recipeRecords.putIfAbsent(recipe.get(), 0);
                      recipeRecords.computeIfPresent(recipe.get(), (k, v) -> v + 1);
                      //                recipeRecords.put(recipe, recipeRecords.get(recipe) + 1);
                      recipe
                          .get()
                          .getIngredients()
                          .forEach(
                              recipeIngredient -> {
                                ingredientsRecord2.putIfAbsent(recipeIngredient, 0);
                                ingredientsRecord2.computeIfPresent(
                                    recipeIngredient, (k, v) -> v + 1);
                                var ingredient =
                                    ingredientService.getIngredientByName(
                                        recipeIngredient.getIngredientName());
                                if (ingredient.isPresent()) {
                                  ingredientsRecord.putIfAbsent(ingredient.get(), 0);
                                  ingredientsRecord.computeIfPresent(
                                      ingredient.get(), (k, v) -> v + 1);
                                  //                        ingredientsRecord.put(ingredient.get(),
                                  //
                                  // ingredientsRecord.get(ingredient.get()) + 1);
                                } else {
                                  ingredientsRecord.putIfAbsent(
                                      new Ingredient(recipeIngredient.getIngredientName() + "*"),
                                      0);
                                  ingredientsRecord.putIfAbsent(
                                      new Ingredient(recipeIngredient.getIngredientName() + "*"),
                                      ingredientsRecord.get(
                                          new Ingredient(
                                              recipeIngredient.getIngredientName() + "*")));
                                }
                              });
                    }
                  });
              var lunch = day.getLunch();
              lunch.forEach(
                  recipeDTO -> {
                    var recipe = recipeService.getRecipeByName(recipeDTO.getRecipeName());
                    if (recipe.isEmpty()) return;
                    recipeRecords.putIfAbsent(recipe.get(), 0);
                    recipeRecords.computeIfPresent(recipe.get(), (k, v) -> v + 1);
                    recipe
                        .get()
                        .getIngredients()
                        .forEach(
                            recipeIngredient -> {
                              ingredientsRecord2.putIfAbsent(recipeIngredient, 0);
                              ingredientsRecord2.computeIfPresent(
                                  recipeIngredient, (k, v) -> v + 1);
                              var ingredient =
                                  ingredientService.getIngredientByName(
                                      recipeIngredient.getIngredientName());
                              if (ingredient.isPresent()) {
                                ingredientsRecord.putIfAbsent(ingredient.get(), 0);
                                ingredientsRecord.computeIfPresent(
                                    ingredient.get(), (k, v) -> v + 1);
                                //                        ingredientsRecord.put(ingredient.get(),
                                //
                                // ingredientsRecord.get(ingredient.get()) + 1);
                              } else {
                                ingredientsRecord.putIfAbsent(
                                    new Ingredient(recipeIngredient.getIngredientName() + "*"), 0);
                                ingredientsRecord.putIfAbsent(
                                    new Ingredient(recipeIngredient.getIngredientName() + "*"),
                                    ingredientsRecord.get(
                                        new Ingredient(
                                            recipeIngredient.getIngredientName() + "*")));
                              }
                            });
                  });
              var dinner = day.getDinner();
              dinner.forEach(
                  recipeDTO -> {
                    var recipe = recipeService.getRecipeByName(recipeDTO.getRecipeName());
                    if (recipe.isPresent()) {

                      recipeRecords.putIfAbsent(recipe.get(), 0);
                      recipeRecords.computeIfPresent(recipe.get(), (k, v) -> v + 1);
                      recipe
                          .get()
                          .getIngredients()
                          .forEach(
                              recipeIngredient -> {
                                ingredientsRecord2.putIfAbsent(recipeIngredient, 0);
                                ingredientsRecord2.computeIfPresent(
                                    recipeIngredient, (k, v) -> v + 1);
                                var ingredient =
                                    ingredientService.getIngredientByName(
                                        recipeIngredient.getIngredientName());
                                if (ingredient.isPresent()) {
                                  ingredientsRecord.putIfAbsent(ingredient.get(), 0);
                                  ingredientsRecord.computeIfPresent(
                                      ingredient.get(), (k, v) -> v + 1);
                                  //                        ingredientsRecord.put(ingredient.get(),
                                  //
                                  // ingredientsRecord.get(ingredient.get()) + 1);
                                } else {
                                  ingredientsRecord.putIfAbsent(
                                      new Ingredient(recipeIngredient.getIngredientName() + "*"),
                                      0);
                                  ingredientsRecord.putIfAbsent(
                                      new Ingredient(recipeIngredient.getIngredientName() + "*"),
                                      ingredientsRecord.get(
                                          new Ingredient(
                                              recipeIngredient.getIngredientName() + "*")));
                                }
                              });
                    }
                  });
            });
    ingredientsRecord
        .keySet()
        .forEach(
            key -> ingredientsRecord1.put(key.getIngredientName(), ingredientsRecord.get(key)));
    return ingredientsRecord1;
  }

  //    @PostMapping(value = "/get", consumes = {MediaType.APPLICATION_JSON_VALUE})
  //    public Map<String, Integer> getShoppingList(@RequestBody Week week) {
  //        Map<String, Integer> ingredientList = getIngredientList(week);
  //        Map<String, Integer> shoppingList = new HashMap<>();
  //
  //
  //        return shoppingList;
  //    }

}
