package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.IngredientRepository;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository repository;

    public Ingredient registerIngredient(String ingredientName) {
        if (getIngredientByName(ingredientName).isEmpty()) {
            return repository.save(new Ingredient(ingredientName));
        } else {
            return getIngredientByName(ingredientName).get();
        }

    }

    public Ingredient registerIngredient(Ingredient newIngredient) {
        return repository.save(newIngredient);
    }

    public Optional<Ingredient> getIngredientByName(String ingredientName) {
        return repository.findById(ingredientName);
    }

    @Transactional
    public Optional<Ingredient> addDependencies(String ingredientName,
                                            Set<Ingredient> dependencies) {
        Optional<Ingredient> targetIngredient = getIngredientByName(ingredientName);
        targetIngredient.ifPresent(ingredient -> {
            dependencies.forEach(dependency -> {
                if (!ingredient.getIngredientName().equals(dependency.getIngredientName()) && repository.findById(
                        dependency.getIngredientName()).isEmpty()) {
                        Ingredient newIngredient;
                    if (StringUtils.isNotBlank(dependency.getDisplayName())&& !dependency.getIngredientName().equals(dependency.getDisplayName())) {
                        newIngredient =
                                registerIngredient(dependency.getDisplayName());
                    } else {
                        newIngredient = registerIngredient(dependency.getIngredientName());
                    }
                    repository.save(newIngredient);
                }
            });
            ingredient.getDependencies().clear();
            ingredient.getDependencies().addAll(dependencies);

        });
        return targetIngredient;
    }

    @Transactional
    public Optional<Ingredient> updateSeasonality(String ingredientName,
                                                                 Set<Ingredient.Season> seasonality) {
        Optional<Ingredient> targetIngredient = repository.findById(
                ingredientName);
        targetIngredient.ifPresent(ingredient -> {
            ingredient.getSeasonality().clear();
            ingredient.getSeasonality().addAll(seasonality);
        });
        return targetIngredient;
    }
}
