package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.recipe.RecipeIngredientsDTO;
import com.mealprep.MealPrep.entities.api.recipe.RecipeMealTypeDTO;
import com.mealprep.MealPrep.entities.api.recipe.RecipeSeasonalityDTO;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.mealprep.MealPrep.entities.recipe.Recipe;
import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;
import com.mealprep.MealPrep.service.RecipeService;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe")
@CrossOrigin(origins = "*")
public class RecipeController {
  @Autowired RecipeService recipeService;

  @PostMapping(
      value = "/get",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public Optional<Recipe> getRecipe(@RequestBody Recipe recipe) {
    return recipeService.getRecipeByName(recipe.getRecipeName());
  }

  @GetMapping(value = "/get/{param}")
  public Iterable<Recipe> getAllRecipe(@PathVariable String param) {
    if (param.equals("all")) {
      return recipeService.getAllRecipes();
    }
    return null;
  }

  @GetMapping(value = "/example")
  public RecipeIngredientsDTO example() {
    Set<Recipe.Season> exampleSeasonSet = new HashSet<>();
    exampleSeasonSet.add(Recipe.Season.SPRING);
    exampleSeasonSet.add(Recipe.Season.SUMMER);
    RecipeSeasonalityDTO example3 = new RecipeSeasonalityDTO("example", exampleSeasonSet);

    Recipe newRecipe = new Recipe("example");
    Ingredient newIngredient = new Ingredient("example ingredient name1");
    RecipeIngredient newRecipeIngredient = new RecipeIngredient("newIngredient");
    Ingredient newIngredient2 = new Ingredient("example ingredient name2");
    RecipeIngredient newRecipeIngredient2 = new RecipeIngredient("newIngredient2");
    newRecipe.getIngredients().add(newRecipeIngredient);
    newRecipe.getIngredients().add(newRecipeIngredient2);

    Set<RecipeIngredient> exampleIngredientSet = new HashSet<>();
    exampleIngredientSet.add(newRecipeIngredient);
    RecipeIngredientsDTO example4 = new RecipeIngredientsDTO("example", exampleIngredientSet);

    return example4;
  }

  @PostMapping(
      value = "/register",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Recipe> registerRecipe(
      @RequestHeader(value = "Authorization") String token, @RequestBody Recipe newRecipe) {

    if (!token.equalsIgnoreCase("test")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(newRecipe);
    }
    return ResponseEntity.status(HttpStatus.OK).body(recipeService.registerRecipe(newRecipe));
  }

  @PatchMapping(
      value = "/ingredients",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<Recipe>> updateIngredients(
      @RequestBody RecipeIngredientsDTO params) {
    Optional<Recipe> result =
        recipeService.updateIngredients(params.getRecipeName(), params.getIngredients());
    if (result.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }
  }

  @PatchMapping(
      value = "/seasonality",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<Recipe>> updateSeasonality(
      @RequestBody RecipeSeasonalityDTO params) {
    Optional<Recipe> result =
        recipeService.updateSeasonality(params.getRecipeName(), params.getSeasonality());
    if (result.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }
  }

  @PatchMapping(
      value = "/type",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<Recipe>> updateMealType(@RequestBody RecipeMealTypeDTO params) {

    String recipeName = params.getRecipeName();
    Optional<Recipe> result = recipeService.updateMealType(recipeName, params.getMealType());
    if (result.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }
  }

  //    @ExceptionHandler({NullPointerException.class,
  //                       IllegalArgumentException.class,
  //                       HttpMessageNotReadableException.class})
  //    @ResponseStatus(HttpStatus.BAD_REQUEST)
  //    public Recipe hanldeReqeustBodyParseError() {
  //        Recipe exampleRecipe = new Recipe("recipe name");
  //        Unit exampleUnit = new Unit("G", "0");
  //        RecipeIngredient newRecipeIngredient =
  //                new RecipeIngredient("example ingredient name", exampleUnit);
  //        exampleRecipe.getIngredients().add(newRecipeIngredient);
  //        System.err.println();
  //        return exampleRecipe;
  //    }

  @ExceptionHandler({MissingRequestHeaderException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public void handleHeaderError() {}
}
