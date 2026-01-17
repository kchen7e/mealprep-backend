package com.mealprep.MealPrep.entities.nutrient;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "nutrient")
public class Nutrient {
  @Id
  @Column(name = "nutrient_name", nullable = false, unique = true)
  private String nutrientName;

  public Nutrient(@NonNull String nutrientName) {
    this.nutrientName = nutrientName.toLowerCase();
  }

  @Override
  public int hashCode() {
    return nutrientName.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Nutrient) {
      return nutrientName.equals(((Nutrient) obj).getNutrientName());
    } else {
      return super.equals(obj);
    }
  }
}
