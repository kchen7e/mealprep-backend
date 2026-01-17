package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.ingredient.IngredientDTO;
import com.mealprep.MealPrep.entities.api.ingredient.IngredientDependenciesDTO;
import com.mealprep.MealPrep.entities.api.ingredient.IngredientSeasonalityDTO;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.mealprep.MealPrep.service.IngredientService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredient")
@CrossOrigin(origins = "*")
@ResponseBody
public class IngredientController {
  @Autowired IngredientService ingredientService;

  @PostMapping(
      value = "/register",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> registerIngredient(
      @RequestHeader(value = "Authorization") String token, @RequestBody IngredientDTO params) {
    if (!token.equalsIgnoreCase("test")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
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

  //    @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class,
  // HttpMessageNotReadableException.class})
  //    @ResponseStatus(HttpStatus.BAD_REQUEST)
  //    public IngredientAPI hanldeReqeustBodyParseError() {
  //        IngredientAPI exampleIngredientAPI = new IngredientAPI("ingredient name");
  //        return exampleIngredientAPI;
  //    }

  @ExceptionHandler({MissingRequestHeaderException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public void handleHeaderError() {}
}
