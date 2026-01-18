package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.ingredient.IngredientDTO;
import com.mealprep.MealPrep.entities.api.ingredient.IngredientDependenciesDTO;
import com.mealprep.MealPrep.entities.api.ingredient.IngredientSeasonalityDTO;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.mealprep.MealPrep.service.IngredientService;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {
  private final IngredientService ingredientService;

  public IngredientController(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
  }

  @PostMapping(
      value = "/register",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> registerIngredient(@RequestBody IngredientDTO params) {
    // Authorization handled by Spring Security filter
    return ResponseEntity.status(HttpStatus.OK)
        .body(ingredientService.registerIngredient(params.getIngredientName()));
  }

  @PostMapping(
      value = "/get",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<Ingredient>> getIngredient(@RequestBody IngredientDTO params) {
    Optional<Ingredient> result = ingredientService.getIngredientByName(params.getIngredientName());
    if (result.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }
  }

  @PatchMapping(
      value = "/dependencies",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<Ingredient>> updateDependencies(
      @RequestBody IngredientDependenciesDTO params) {
    Optional<Ingredient> result =
        ingredientService.addDependencies(params.getIngredientName(), params.getIngredients());
    if (result.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }
  }

  @PatchMapping(
      value = "/seasonality",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<Ingredient>> updateSeasonality(
      @RequestBody IngredientSeasonalityDTO params) {
    Optional<Ingredient> result =
        ingredientService.updateSeasonality(params.getIngredientName(), params.getSeasonality());
    if (result.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    } else {
      return ResponseEntity.status(HttpStatus.OK).body(result);
    }
  }
}
