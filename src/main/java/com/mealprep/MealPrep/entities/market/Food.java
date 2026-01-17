package com.mealprep.MealPrep.entities.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.mealprep.MealPrep.measures.Unit;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Table(name = "food")
@Entity
public class Food {

  public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
  }

  //    @Id
  //    @GeneratedValue(strategy = GenerationType.AUTO)
  //    @Column(name = "id", nullable = false)
  //    @JsonProperty("id")
  //    private Long id;

  @Id
  @JsonProperty("foodName")
  @Column(name = "food_name")
  String foodName;

  @JsonProperty("displayName")
  @Column(name = "display_name")
  String displayName;

  @Setter
  @Column(name = "category")
  String category;

  @Setter
  @Column(name = "readily_available")
  boolean readilyAvailable;

  @Setter
  @Column(name = "unit")
  Unit unit;

  @Column(name = "nutrients")
  @JsonProperty("nutrients")
  @ManyToMany
  @JoinColumn(name = "nutrient", referencedColumnName = "nutrient_name")
  private final Set<Nutrient> nutrients;

  @Column(name = "seasonality")
  @ElementCollection
  @JsonProperty("seasonality")
  private final Set<Food.Season> seasonality;

  @Column(name = "commercial_form")
  @ElementCollection
  @JsonProperty("foundAs")
  @Setter
  private Set<CommercialForm> foundAs;

  @Column(name = "for_ingredients")
  @ManyToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "ingredient", referencedColumnName = "ingredient_name")
  private final Set<Ingredient> forIngredients;

  public Food() {
    seasonality = new HashSet<>();
    nutrients = new HashSet<>();
    foundAs = new HashSet<>();
    forIngredients = new HashSet<>();
    unit = new Unit();
    readilyAvailable = true;
    category = "";
  }

  public Food(String foodName) {
    displayName = foodName;
    this.foodName = foodName.toLowerCase();
    seasonality = new HashSet<>();
    nutrients = new HashSet<>();
    foundAs = new HashSet<>();
    forIngredients = new HashSet<>();
    unit = new Unit();
    readilyAvailable = true;
  }

  public void setFoodName(String foodName) {
    displayName = foodName;
    this.foodName = foodName.toLowerCase();
  }
}
