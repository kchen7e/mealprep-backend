package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.NutrientRepository;
import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NutrientService {
  @Autowired NutrientRepository repository;

  public Optional<Nutrient> getNutrientByName(String nutrientName) {
    return repository.findById(nutrientName);
  }

  public Optional<Nutrient> registerNutrient(String nutrientName) {
    if (getNutrientByName(nutrientName).isEmpty()) {
      return Optional.of(repository.save(new Nutrient(nutrientName)));
    } else {
      return getNutrientByName(nutrientName);
    }
  }
}
