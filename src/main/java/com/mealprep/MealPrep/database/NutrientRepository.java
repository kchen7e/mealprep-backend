package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.nutrient.Nutrient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientRepository extends CrudRepository<Nutrient, String> {}
