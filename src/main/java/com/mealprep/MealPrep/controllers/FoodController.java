package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.food.FoodCommercialFormDTO;
import com.mealprep.MealPrep.entities.market.CommercialForm;
import com.mealprep.MealPrep.entities.market.Food;
import com.mealprep.MealPrep.service.FoodService;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/food")
@CrossOrigin(origins = "*")
@ResponseBody
public class FoodController {

  @Autowired FoodService foodService;

  @PostMapping(
      value = "/register",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> registerFood(
      @RequestHeader(value = "Authorization") String token, @RequestBody Food newFood) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(newFood);
    //        if (!token.equalsIgnoreCase("test")) {
    //            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    //        }
    //        return ResponseEntity.status(HttpStatus.OK).body(
    //                foodService.registerFood(params.getFoodName()));
  }

  @PatchMapping(
      value = "/foundas",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Object> updateCommercialForms(
      @RequestHeader(value = "Authorization") String token,
      @RequestBody FoodCommercialFormDTO params) {
    if (!token.equalsIgnoreCase("test")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
    }
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
