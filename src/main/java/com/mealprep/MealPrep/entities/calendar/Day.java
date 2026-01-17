package com.mealprep.MealPrep.entities.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealprep.MealPrep.entities.api.recipe.RecipeDTO;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Day {
  @JsonProperty("breakfast")
  private List<RecipeDTO> breakfast;

  @JsonProperty("lunch")
  private List<RecipeDTO> lunch;

  @JsonProperty("dinner")
  private List<RecipeDTO> dinner;
}
