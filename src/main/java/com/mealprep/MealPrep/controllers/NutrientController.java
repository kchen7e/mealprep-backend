package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import com.mealprep.MealPrep.service.NutrientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/nutrient")
@CrossOrigin(origins = "*")
@ResponseBody
public class NutrientController {
    @Autowired
    NutrientService nutrientService;

    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Optional<Nutrient> registerNutrient(@RequestBody Map<String, String> params) {
        if (!params.containsKey("nutrientName") || params.get("nutrientName").isBlank()) {
            return Optional.empty();
        } else {
            String nutrientName = params.get("nutrientName").toLowerCase();
            return nutrientService.registerNutrient(nutrientName);
        }
    }

    @PostMapping(value = "/get", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public Optional<Nutrient> getNutrient(
            @RequestBody Map<String, String> params) {
        if (params.get("nutrientName").isEmpty()) {
            return Optional.empty();
        } else {
            String nutrientName = params.get("nutrientName").toLowerCase();
            return nutrientService.getNutrientByName(nutrientName);
        }

    }
}
