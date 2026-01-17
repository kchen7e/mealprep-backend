package com.mealprep.MealPrep.entities.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Entity
@Getter
@Table(name = "recipe")
public class Recipe {

  public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
  }

  public enum MealType {
    BREAKFAST,
    BRUNCH,
    LUNCH,
    DINNER,
    SUPPER,
    DESSERT
  }

  @Id
  @Column(name = "recipe_name")
  @JsonProperty("recipeName")
  private String recipeName;

  @Column(name = "display_name")
  @JsonProperty("displayName")
  private String displayName;

  @Setter
  @JsonProperty("ingredients")
  @JoinColumn(name = "ingredient", referencedColumnName = "ingredient_name")
  @ElementCollection
  private Set<RecipeIngredient> ingredients;

  @Setter
  @Column(name = "seasonality")
  @ElementCollection
  @JsonProperty("seasonality")
  private Set<Season> seasonality;

  @Setter
  @Column(name = "meal_type")
  @ElementCollection
  @JsonProperty("mealType")
  private Set<MealType> mealType;

  @Setter
  @Column(name = "image_url")
  @JsonProperty("imageUrl")
  private String imageUrl;

  public Recipe() {
    ingredients = new HashSet<>();
    seasonality = new HashSet<>();
    mealType = new HashSet<>();
  }

  public Recipe(String recipeName) {
    displayName = recipeName;
    this.recipeName = recipeName.toLowerCase();
    ingredients = new HashSet<>();
    seasonality = new HashSet<>();
    mealType = new HashSet<>();
  }

  public void setRecipeName(String recipeName) {
    displayName = recipeName;
    this.recipeName = recipeName.toLowerCase();
  }

  //    public void setIngredient(String ingredientName, Unit unit) {
  //        ingredients.add(new RecipeIngredient(ingredientName,unit));
  //    }
  //
  //    public void addIngredient(Ingredient ingredient, Unit unit) {
  //        ingredients.add(new RecipeIngredient(ingredient.getIngredientName(), unit));
  //    }

  @Override
  public int hashCode() {
    return recipeName.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Recipe) {
      return recipeName.equals(((Recipe) obj).getRecipeName())
          && ingredients.equals(((Recipe) obj).getIngredients())
          && seasonality.equals(((Recipe) obj).getSeasonality())
          && mealType.equals(((Recipe) obj).getMealType());
    } else {
      return super.equals(obj);
    }
  }
}
