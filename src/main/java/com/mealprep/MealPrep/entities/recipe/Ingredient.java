package com.mealprep.MealPrep.entities.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ingredient")
public class Ingredient {

  public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER
  }

  @Id
  @Column(name = "ingredient_name")
  @JsonProperty("ingredientName")
  private String ingredientName;

  @Column(name = "display_name", nullable = false, unique = true)
  @JsonProperty("displayName")
  private String displayName;

  @Setter
  @JsonProperty("nutrients")
  @ManyToMany(fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "ingredient_nutrient",
      joinColumns = @JoinColumn(name = "ingredient_name", referencedColumnName = "ingredient_name"),
      inverseJoinColumns =
          @JoinColumn(name = "nutrient_name", referencedColumnName = "nutrient_name"))
  private Set<Nutrient> nutrients;

  @Setter
  @JsonProperty("dependencies")
  @ManyToMany(fetch = FetchType.LAZY)
  @Fetch(FetchMode.SUBSELECT)
  @JoinTable(
      name = "ingredient_dependency",
      joinColumns = @JoinColumn(name = "ingredient_name", referencedColumnName = "ingredient_name"),
      inverseJoinColumns =
          @JoinColumn(name = "dependent_ingredient_name", referencedColumnName = "ingredient_name"))
  private Set<Ingredient> dependencies;

  @Setter
  @Column(name = "seasonality")
  @ElementCollection
  @JsonProperty("seasonality")
  private Set<Ingredient.Season> seasonality;

  @Setter
  @Column(name = "image_url")
  @JsonProperty("imageUrl")
  private String imageUrl;

  public Ingredient(String ingredientName) {
    displayName = ingredientName;
    this.ingredientName = ingredientName.toLowerCase();
    nutrients = new HashSet<>();
    dependencies = new HashSet<>();
  }

  public void setIngredientName(String ingredientName) {
    displayName = ingredientName;
    this.ingredientName = ingredientName.toLowerCase();
  }

  @Override
  public int hashCode() {
    return ingredientName.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Ingredient) {
      return ingredientName.equals(((Ingredient) obj).getIngredientName());
    } else {
      return super.equals(obj);
    }
  }
}
