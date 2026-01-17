package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.FoodRepository;
import com.mealprep.MealPrep.entities.market.CommercialForm;
import com.mealprep.MealPrep.entities.market.Food;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodService {
  @Autowired private FoodRepository repository;

  @Transactional
  public Food registerFood(@NonNull String foodName) {
    if (getFoodByName(foodName).isEmpty()) {
      return repository.save(new Food(foodName));
    } else {
      return getFoodByName(foodName).get();
    }
  }

  @Transactional
  public Food registerFood(@NonNull Food food) {

    if (getFoodByName(food.getFoodName()).isEmpty()) {
      return repository.save(food);
    } else {
      return food;
    }
  }

  @Transactional
  public Optional<Food> addCommercialForm(@NonNull Food food, CommercialForm cf) {
    Optional<Food> targetFood = repository.findById(food.getFoodName());
    targetFood.ifPresent(value -> value.getFoundAs().add(cf));
    return targetFood;
  }

  @Transactional
  public Optional<Food> updateCommercialForm(@NonNull String foodName, Set<CommercialForm> forms) {
    Optional<Food> targetFood = repository.findById(foodName);
    targetFood.ifPresent(
        target -> {
          target.getFoundAs().clear();
          target.getFoundAs().addAll(forms);
        });
    return targetFood;
  }

  public Optional<Food> getFoodByName(String foodName) {
    return repository.findById(foodName);
  }
}
