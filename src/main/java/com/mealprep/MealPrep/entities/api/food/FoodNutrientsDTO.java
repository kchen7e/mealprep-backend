package com.mealprep.MealPrep.entities.api.food;

import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class FoodNutrientsDTO extends FoodDTO {

    @Getter
    @Setter
    public Set<Nutrient> nutrients;


//    public FoodNutrientsAPI(@NotNull String foodName) {
//        super(foodName);
//    }

    public FoodNutrientsDTO(@NotNull String foodName,
                            Set<Nutrient> nutrients) {
        super(foodName);
        this.nutrients = nutrients;
    }

}
