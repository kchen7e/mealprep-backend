package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.IngredientRepository;
import com.mealprep.MealPrep.database.RecipeRepository;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import com.mealprep.MealPrep.entities.recipe.Recipe;
import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;
import com.sun.istack.NotNull;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;


@Service
public class RecipeService {
    @Autowired
    private RecipeRepository repository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @Transactional
    public Recipe registerRecipe(@NonNull Recipe newRecipe) {
        if (repository.findById(newRecipe.getRecipeName()).isPresent()) {
            return repository.findById(newRecipe.getRecipeName()).get();
        }

        if (!CollectionUtils.isEmpty(newRecipe.getIngredients())) {
            newRecipe.getIngredients().forEach(recipeIngredient -> {
                if (ingredientRepository.findById(recipeIngredient.getIngredientName()).isEmpty()) {
                    Ingredient newIngredient =
                            new Ingredient(recipeIngredient.getDisplayName());
                    ingredientRepository.save(newIngredient);
                }
            });
        }
        Recipe consolidatedNewRecipe;
        if (StringUtils.isNotBlank(newRecipe.getDisplayName()) && !newRecipe.getDisplayName().equals(newRecipe.getRecipeName())) {
            consolidatedNewRecipe = new Recipe(newRecipe.getDisplayName());
        } else {
            consolidatedNewRecipe = new Recipe(newRecipe.getRecipeName());
        }
        consolidatedNewRecipe.setIngredients(newRecipe.getIngredients());
        consolidatedNewRecipe.setMealType(newRecipe.getMealType());
        consolidatedNewRecipe.setSeasonality(newRecipe.getSeasonality());
        return repository.save(consolidatedNewRecipe);
    }

    public Optional<Recipe> getRecipeByName(String recipeName) {
        return repository.findById(recipeName);
    }

    public Iterable<Recipe> getAllRecipes() {
        return repository.findAll();
    }

    @Transactional
    public Optional<Recipe> updateSeasonality(String recipeName,
                                              List<String> seasonality) {
        Optional<Recipe> targetRecipe = repository.findById(recipeName);
        List<Recipe.Season> seasonalityEnum = seasonality.stream().map(
                Recipe.Season::valueOf).toList();
        targetRecipe.ifPresent(recipe -> {
            recipe.getSeasonality().clear();
            recipe.getSeasonality().addAll(seasonalityEnum);
        });
        return targetRecipe;
    }

    @Transactional
    public Optional<Recipe> updateSeasonality(String recipeName,
                                              Set<Recipe.Season> seasonality) {

        Optional<Recipe> targetRecipe = repository.findById(recipeName);

        targetRecipe.ifPresent( recipe -> {
            targetRecipe.get().getSeasonality().clear();
            targetRecipe.get().getSeasonality().addAll(seasonality);
        });
        return targetRecipe;
    }

    @Transactional
    public Optional<Recipe> addIngredient(@NotNull  String recipeName,
                                          @NotNull RecipeIngredient recipeIngredient) {
        Optional<Recipe> targetRecipe = repository.findById(recipeName);
        if (targetRecipe.isPresent()) {
            if (targetRecipe.get().getIngredients().stream().noneMatch(
                    existingIngredient -> existingIngredient.getIngredientName().equals(
                            recipeIngredient.getIngredientName()))) {
                targetRecipe.get().getIngredients().add(recipeIngredient);
            }
            if (ingredientRepository.findById(recipeIngredient.getIngredientName()).isEmpty()) {
                Ingredient newIngredient = new Ingredient(recipeIngredient.getIngredientName());
                ingredientRepository.save(newIngredient);
            }
        }
        return repository.findById(recipeName);
    }

    @Transactional
    public Optional<Recipe> updateIngredients(String recipeName,
                                           Set<RecipeIngredient> ingredients) {
        Optional<Recipe> targetRecipe = getRecipeByName(recipeName);
        if (targetRecipe.isPresent()) {
            ingredients.forEach(
                    ingredient -> {
                        if (ingredientRepository.findById(ingredient.getIngredientName()).isEmpty()) {
                            Ingredient newIngredient =
                                    new Ingredient(ingredient.getIngredientName());
                            ingredientRepository.save(newIngredient);
                        }
                    });
            if (!targetRecipe.get().getIngredients().equals(ingredients)) {
                targetRecipe.get().getIngredients().clear();
                targetRecipe.get().getIngredients().addAll(ingredients);
            }
        }
        return repository.findById(recipeName);
    }

    @Transactional
    public Optional<Recipe> updateMealType(String recipeName,
                                           Set<Recipe.MealType> mealType) {
        Optional<Recipe> targetRecipe = repository.findById(recipeName);
        targetRecipe.ifPresent(recipe -> {
                recipe.getMealType().clear();
                recipe.getMealType().addAll(mealType);
        });
        return targetRecipe;
    }

}
