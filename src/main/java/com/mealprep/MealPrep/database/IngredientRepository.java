package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.recipe.Ingredient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, String> {

  /**
   * Inserts an ingredient if it doesn't exist (safe for concurrent writes). Uses DO UPDATE (no-op)
   * instead of DO NOTHING to release PostgreSQL locks immediately.
   *
   * @param ingredientName Primary key (unique)
   * @param displayName Human-readable name
   */
  @Modifying
  @Query(
      value =
          "INSERT INTO ingredient (ingredient_name, display_name) "
              + "VALUES (:ingredientName, :displayName) "
              + "ON CONFLICT (ingredient_name) DO UPDATE SET "
              + "display_name = ingredient.display_name WHERE false", // No-op update (avoids
      // changing data)
      nativeQuery = true)
  void insertIfNotExists(
      @Param("ingredientName") String ingredientName, @Param("displayName") String displayName);
}
