package com.mealprep.MealPrep.entities;

import com.mealprep.MealPrep.entities.calendar.Week;
import com.mealprep.MealPrep.entities.market.Food;
import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ShoppingList {
  ArrayList<Food> list;
  Week weeklyRecipes;

  public ShoppingList(Week week) {
    this.weeklyRecipes = week;
  }
}
