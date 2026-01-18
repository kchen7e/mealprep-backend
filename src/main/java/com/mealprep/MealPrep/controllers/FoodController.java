package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.food.FoodCommercialFormDTO;
import com.mealprep.MealPrep.entities.market.CommercialForm;
import com.mealprep.MealPrep.entities.market.Food;
import com.mealprep.MealPrep.service.FoodService;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food")
public class FoodController {

  private final FoodService foodService;

  public FoodController(FoodService foodService) {
    this.foodService = foodService;
  }

  @PostMapping(
      value = "/register",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> registerFood(@RequestBody Food newFood) {
    // Authorization handled by Spring Security filter
    return ResponseEntity.status(HttpStatus.OK).body(foodService.registerFood(newFood));
  }

  @PatchMapping(
      value = "/foundas",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> updateCommercialForms(@RequestBody FoodCommercialFormDTO params) {
    // Authorization handled by Spring Security filter
    return ResponseEntity.status(HttpStatus.OK)
        .body(foodService.updateCommercialForm(params.getFoodName(), params.getFoundAs()));
  }

  @GetMapping(value = "/example")
  public ResponseEntity<Object> example() {
    Food newFood = new Food("example");
    foodService.registerFood(newFood);
    Set<CommercialForm> cfs = new HashSet();
    CommercialForm cf = new CommercialForm(CommercialForm.SaleUnitType.CAN, 1, "Woolworths");
    cfs.add(cf);
    return ResponseEntity.status(HttpStatus.OK)
        .body(foodService.updateCommercialForm("example", cfs));
  }
}
