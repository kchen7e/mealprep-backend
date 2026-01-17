package com.mealprep.MealPrep.entities.api.food;

import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class FoodNutrientsDTO extends FoodDTO {

  @Getter @Setter public Set<Nutrient> nutrients;

  //    public FoodNutrientsAPI(@NonNull String foodName) {
  //        super(foodName);
  //    }

  public FoodNutrientsDTO(@NonNull String foodName, Set<Nutrient> nutrients) {
    super(foodName);
    this.nutrients = nutrients;
  }
}
