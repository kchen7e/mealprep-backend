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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

  @Id
  @JsonProperty("foodName")
  @Column(name = "food_name", nullable = false, unique = true) // Explicit unique constraint
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

  @JsonProperty("nutrients")
  @ManyToMany(fetch = FetchType.LAZY) // No cascading here either (Nutrient is also a lookup table)
  @JoinTable(
      name = "food_nutrient",
      joinColumns = @JoinColumn(name = "food_name", referencedColumnName = "food_name"),
      inverseJoinColumns =
          @JoinColumn(name = "nutrient_name", referencedColumnName = "nutrient_name"))
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

  // ✅ FIX: Removed CascadeType.PERSIST/MERGE (critical for deadlock prevention)
  @ManyToMany(fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "food_ingredient",
      joinColumns = @JoinColumn(name = "food_name", referencedColumnName = "food_name"),
      inverseJoinColumns =
          @JoinColumn(name = "ingredient_name", referencedColumnName = "ingredient_name"))
  private final Set<Ingredient> forIngredients;

  // ✅ Improved constructor (avoids nulls for collections)
  public Food() {
    this.seasonality = new HashSet<>();
    this.nutrients = new HashSet<>();
    this.foundAs = new HashSet<>();
    this.forIngredients = new HashSet<>();
    this.unit = new Unit();
    this.readilyAvailable = true;
    this.category = "";
  }

  public Food(String foodName) {
    this.displayName = foodName;
    this.foodName = foodName.toLowerCase();
    this.seasonality = new HashSet<>();
    this.nutrients = new HashSet<>();
    this.foundAs = new HashSet<>();
    this.forIngredients = new HashSet<>();
    this.unit = new Unit();
    this.readilyAvailable = true;
  }

  public void setFoodName(String foodName) {
    this.displayName = foodName;
    this.foodName = foodName.toLowerCase();
  }

  // ✅ Helper method to add ingredients (controlled insertion)
  public void addIngredient(Ingredient ingredient) {
    this.forIngredients.add(ingredient);
  }
}
