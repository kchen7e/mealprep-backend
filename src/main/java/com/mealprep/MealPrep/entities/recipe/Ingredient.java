package com.mealprep.MealPrep.entities.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ingredient")
public class Ingredient {

    public enum Season {
        SPRING, SUMMER, AUTUMN, WINTER
    }
    @Id
    @Column(name = "ingredient_name")
    @JsonProperty("ingredientName")
    private String ingredientName;


    @Column(name = "display_name", nullable = false, unique = true)
    @JsonProperty("displayName")
    private String displayName;


    @Setter
    @Column(name = "nutrients")
    @JsonProperty("nutrients")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrient", referencedColumnName = "nutrient_name")
    private Set<Nutrient> nutrients;

    @Setter
    @Column(name = "dependencies")
    @JsonProperty("dependencies")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient", referencedColumnName = "ingredient_name")
    private Set<Ingredient> dependencies;

    @Setter
    @Column(name = "seasonality")
    @ElementCollection
    @JsonProperty("seasonality")
    private Set<Ingredient.Season> seasonality;


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
            return ingredientName.equals(
                    ((Ingredient) obj).getIngredientName());
        } else {
            return super.equals(obj);
        }
    }

}
