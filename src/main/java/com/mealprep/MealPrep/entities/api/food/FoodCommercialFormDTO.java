package com.mealprep.MealPrep.entities.api.food;

import com.mealprep.MealPrep.entities.market.CommercialForm;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

public class FoodCommercialFormDTO extends FoodDTO {
  @Getter @Setter public Set<CommercialForm> foundAs;

  //    public FoodCommercialFormAPI(@NonNull String foodName) {
  //        super(foodName);
  //    }

  public FoodCommercialFormDTO(@NonNull String foodName, Set<CommercialForm> forms) {
    super(foodName);
    this.foundAs = forms;
  }
}
