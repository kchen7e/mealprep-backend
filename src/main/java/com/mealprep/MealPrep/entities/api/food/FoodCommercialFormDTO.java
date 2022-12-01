package com.mealprep.MealPrep.entities.api.food;

import com.mealprep.MealPrep.entities.market.CommercialForm;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

public class FoodCommercialFormDTO extends FoodDTO {
    @Getter
    @Setter
    public Set<CommercialForm> foundAs;


//    public FoodCommercialFormAPI(@NotNull String foodName) {
//        super(foodName);
//    }

    public FoodCommercialFormDTO(@NotNull String foodName,
                                 Set<CommercialForm> forms) {
        super(foodName);
        this.foundAs = forms;
    }
}
