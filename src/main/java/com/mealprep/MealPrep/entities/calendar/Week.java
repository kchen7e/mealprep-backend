package com.mealprep.MealPrep.entities.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;

@Data
public class Week {

  @JsonProperty("0")
  private Day day1 = null;

  @JsonProperty("1")
  private Day day2 = null;

  @JsonProperty("2")
  private Day day3 = null;

  @JsonProperty("3")
  private Day day4 = null;

  @JsonProperty("4")
  private Day day5 = null;

  @JsonProperty("5")
  private Day day6 = null;

  @JsonProperty("6")
  private Day day7 = null;

  public Week() {
    day1 = new Day();
    day2 = new Day();
    day3 = new Day();
    day4 = new Day();
    day5 = new Day();
    day6 = new Day();
    day7 = new Day();
  }

  public ArrayList<Day> getWeek() {
    return new ArrayList<>(Arrays.asList(day1, day2, day3, day4, day5, day6, day7));
  }
}
