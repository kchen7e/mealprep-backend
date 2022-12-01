package com.mealprep.MealPrep.entities.api.food;

import com.mealprep.MealPrep.entities.market.Food;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class FoodDTO {
    @Getter
    private String foodName;

    @Getter
    private String displayName;

    @Getter
    private Set<Food.Season> seasonality;

    public FoodDTO(@NotNull String foodName) {
        displayName = foodName;
        this.foodName = foodName.toLowerCase();
        seasonality = new HashSet<>();
    }

    public void setFoodName(String foodName) {
        displayName = foodName;
        this.foodName = foodName.toLowerCase();
    }

    public void setSeasonality(List<String> seasonality) {
        seasonality.forEach(
                season ->
                        this.seasonality.add(Food.Season.valueOf(season)));
    }

}
