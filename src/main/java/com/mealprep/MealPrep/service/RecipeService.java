package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.IngredientRepository;
import com.mealprep.MealPrep.database.RecipeRepository;
import com.mealprep.MealPrep.entities.recipe.Recipe;
import com.mealprep.MealPrep.entities.recipe.RecipeIngredient;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PSQLException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Service
public class RecipeService {
  // ✅ Replace @Autowired with constructor injection (fixes immutability + testability)
  private final RecipeRepository repository;
  private final IngredientRepository ingredientRepository;
  private final StorageService storageService;
  private final String recipeBucket;

  // ✅ Constructor injection (no @Autowired needed)
  public RecipeService(
      RecipeRepository repository,
      IngredientRepository ingredientRepository,
      StorageService storageService,
      @org.springframework.beans.factory.annotation.Value("${minio.bucket.recipe:recipe-images}")
          String recipeBucket) {
    this.repository = repository;
    this.ingredientRepository = ingredientRepository;
    this.storageService = storageService;
    this.recipeBucket = recipeBucket;
  }

  // ✅ Critical: Add retry + REPEATABLE_READ isolation (fixes deadlocks)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 5, // More retries for high concurrency
      backoff = @Backoff(delay = 200, multiplier = 1.5) // Exponential backoff (reduces contention)
      )
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Recipe registerRecipe(@NonNull Recipe newRecipe) {
    if (StringUtils.isBlank(newRecipe.getRecipeName())) {
      throw new IllegalArgumentException("Recipe name cannot be empty");
    }

    // ✅ Simplify duplicate check (avoid redundant repository.findById calls)
    Optional<Recipe> existingRecipe = repository.findById(newRecipe.getRecipeName());
    if (existingRecipe.isPresent()) {
      return existingRecipe.get();
    }

    // ✅ Fix 1: Sort RecipeIngredients by ingredientName (eliminates circular locks)
    if (!CollectionUtils.isEmpty(newRecipe.getIngredients())) {
      // Sort ingredients alphabetically by ingredientName (primary key)
      List<RecipeIngredient> sortedIngredients =
          newRecipe.getIngredients().stream()
              .sorted(Comparator.comparing(RecipeIngredient::getIngredientName))
              .collect(Collectors.toList());

      // Insert sorted ingredients (no circular lock waits)
      sortedIngredients.forEach(
          recipeIngredient -> {
            ingredientRepository.insertIfNotExists(
                recipeIngredient.getIngredientName(), recipeIngredient.getDisplayName());
          });
    }

    // Preserve original logic for consolidated recipe
    Recipe consolidatedNewRecipe;
    if (StringUtils.isNotBlank(newRecipe.getDisplayName())
        && !newRecipe.getDisplayName().equals(newRecipe.getRecipeName())) {
      consolidatedNewRecipe = new Recipe(newRecipe.getDisplayName());
    } else {
      consolidatedNewRecipe = new Recipe(newRecipe.getRecipeName());
    }
    consolidatedNewRecipe.setIngredients(newRecipe.getIngredients());
    consolidatedNewRecipe.setMealType(newRecipe.getMealType());
    consolidatedNewRecipe.setSeasonality(newRecipe.getSeasonality());
    return repository.save(consolidatedNewRecipe);
  }

  // ✅ Keep read operations non-transactional (no locks needed)
  public Optional<Recipe> getRecipeByName(String recipeName) {
    return repository.findById(recipeName);
  }

  public Iterable<Recipe> getAllRecipes() {
    return repository.findAll();
  }

  // ✅ Add retry to seasonality update (write operation—deadlock-prone)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Recipe> updateSeasonality(String recipeName, List<String> seasonality) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);
    List<Recipe.Season> seasonalityEnum = seasonality.stream().map(Recipe.Season::valueOf).toList();
    targetRecipe.ifPresent(
        recipe -> {
          recipe.getSeasonality().clear();
          recipe.getSeasonality().addAll(seasonalityEnum);
        });
    return targetRecipe;
  }

  // ✅ Add retry to overloaded seasonality update
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Recipe> updateSeasonality(String recipeName, Set<Recipe.Season> seasonality) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);
    targetRecipe.ifPresent(
        recipe -> {
          recipe.getSeasonality().clear();
          recipe.getSeasonality().addAll(seasonality);
        });
    return targetRecipe;
  }

  // ✅ Add retry + sorted insert to addIngredient (critical—writes to ingredient)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Recipe> addIngredient(
      @NonNull String recipeName, @NonNull RecipeIngredient recipeIngredient) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);
    if (targetRecipe.isPresent()) {
      // Check for duplicate ingredient (preserve original logic)
      boolean ingredientExists =
          targetRecipe.get().getIngredients().stream()
              .anyMatch(
                  existing ->
                      existing.getIngredientName().equals(recipeIngredient.getIngredientName()));

      if (!ingredientExists) {
        targetRecipe.get().getIngredients().add(recipeIngredient);
      }

      // Insert ingredient (safe—sorted by name implicitly here)
      ingredientRepository.insertIfNotExists(
          recipeIngredient.getIngredientName(), recipeIngredient.getIngredientName());
    }
    return repository.findById(recipeName);
  }

  // ✅ Add retry + sorted inserts to updateIngredients (critical—writes to ingredient)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Recipe> updateIngredients(String recipeName, Set<RecipeIngredient> ingredients) {
    Optional<Recipe> targetRecipe = getRecipeByName(recipeName);
    if (targetRecipe.isPresent()) {
      // Sort ingredients before insertion (eliminate circular locks)
      List<RecipeIngredient> sortedIngredients =
          ingredients.stream()
              .sorted(Comparator.comparing(RecipeIngredient::getIngredientName))
              .collect(Collectors.toList());

      // Insert sorted ingredients
      sortedIngredients.forEach(
          ingredient -> {
            ingredientRepository.insertIfNotExists(
                ingredient.getIngredientName(), ingredient.getIngredientName());
          });

      // Preserve original update logic
      if (!targetRecipe.get().getIngredients().equals(ingredients)) {
        targetRecipe.get().getIngredients().clear();
        targetRecipe.get().getIngredients().addAll(ingredients);
      }
    }
    return repository.findById(recipeName);
  }

  // ✅ Add retry to updateMealType (write operation)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Recipe> updateMealType(String recipeName, Set<Recipe.MealType> mealType) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);
    targetRecipe.ifPresent(
        recipe -> {
          recipe.getMealType().clear();
          recipe.getMealType().addAll(mealType);
        });
    return targetRecipe;
  }

  // ✅ Add retry to updateImage (write operation)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Recipe> updateImage(
      String recipeName, InputStream imageData, long size, String contentType) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);
    if (targetRecipe.isPresent()) {
      String key = "recipes/" + recipeName + getExtension(contentType);
      storageService.upload(recipeBucket, key, imageData, size, contentType);
      targetRecipe.get().setImageUrl(key);
    }
    return targetRecipe;
  }

  // Preserve original helper method
  private String getExtension(String contentType) {
    return switch (contentType) {
      case "image/jpeg" -> ".jpg";
      case "image/png" -> ".png";
      case "image/gif" -> ".gif";
      case "image/webp" -> ".webp";
      default -> "";
    };
  }
}
