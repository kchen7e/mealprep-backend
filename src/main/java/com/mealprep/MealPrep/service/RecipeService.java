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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Service for managing Recipe operations with optimized concurrency and retry logic. Fixes critical
 * issues: 1. Retry logic wrapping transactions (prevents dirty connection reuse) 2. Exponential
 * backoff for retries (reduces database lock contention) 3. Sorted ingredient inserts (eliminates
 * circular deadlocks) 4. New transaction per retry (avoids isolation level modification errors)
 */
@Service
public class RecipeService {
  // Logger for retry/error tracking (critical for production debugging)
  private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

  // Immutable dependencies (constructor injection for testability/immutability)
  private final RecipeRepository repository;
  private final IngredientRepository ingredientRepository;
  private final StorageService storageService;
  private final String recipeBucket;

  /**
   * Constructor injection (no @Autowired needed - Spring 4.3+)
   *
   * @param repository Recipe data access layer
   * @param ingredientRepository Ingredient data access layer
   * @param storageService MinIO storage service for image uploads
   * @param recipeBucket MinIO bucket name for recipe images (from config)
   */
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

  /**
   * Registers a new recipe with concurrency safeguards: - Retry on lock contention/PostgreSQL
   * errors (5 attempts max) - Exponential backoff (200ms → 300ms → 450ms → ... up to 2s) - New
   * transaction per retry (avoids isolation level modification errors) - Sorted ingredient inserts
   * (prevents circular deadlocks)
   *
   * @param newRecipe Recipe to register (must have non-empty name)
   * @return Registered/existing recipe (idempotent operation)
   * @throws IllegalArgumentException If recipe name is empty
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 5, // Optimized for high concurrency (balance between retries and latency)
      backoff =
          @Backoff(
              delay = 200,
              multiplier = 1.5,
              maxDelay = 2000) // Exponential backoff to reduce contention
      )
  @Transactional(
      propagation =
          Propagation
              .REQUIRES_NEW, // Critical: New transaction for every retry (avoids dirty connections)
      rollbackFor = Exception.class // Ensure all exceptions trigger transaction rollback
      )
  public Recipe registerRecipe(@NonNull Recipe newRecipe) {
    // Validate required field (fail fast)
    if (StringUtils.isBlank(newRecipe.getRecipeName())) {
      throw new IllegalArgumentException("Recipe name cannot be empty or blank");
    }

    // Idempotent check - return existing recipe if present (avoids duplicate inserts)
    Optional<Recipe> existingRecipe = repository.findById(newRecipe.getRecipeName());
    if (existingRecipe.isPresent()) {
      log.debug("Recipe {} already exists - returning existing", newRecipe.getRecipeName());
      return existingRecipe.get();
    }

    // Critical: Sort ingredients alphabetically to eliminate circular deadlocks
    // Deadlocks occur when multiple transactions acquire locks in different orders
    if (!CollectionUtils.isEmpty(newRecipe.getIngredients())) {
      List<RecipeIngredient> sortedIngredients =
          newRecipe.getIngredients().stream()
              .sorted(Comparator.comparing(RecipeIngredient::getIngredientName))
              .collect(Collectors.toList());

      // Insert ingredients in sorted order (consistent lock acquisition)
      sortedIngredients.forEach(
          ingredient -> {
            ingredientRepository.insertIfNotExists(
                ingredient.getIngredientName(), ingredient.getDisplayName());
          });
    }

    // Preserve original display name logic
    Recipe consolidatedNewRecipe = createConsolidatedRecipe(newRecipe);
    consolidatedNewRecipe.setIngredients(newRecipe.getIngredients());
    consolidatedNewRecipe.setMealType(newRecipe.getMealType());
    consolidatedNewRecipe.setSeasonality(newRecipe.getSeasonality());

    Recipe savedRecipe = repository.save(consolidatedNewRecipe);
    log.debug("Successfully registered recipe: {}", savedRecipe.getRecipeName());
    return savedRecipe;
  }

  /**
   * Recovery method for registerRecipe retries (triggers when max attempts fail) Prevents unhandled
   * exceptions and provides meaningful error logging
   *
   * @param e Exception that caused retry failure
   * @param newRecipe Original recipe object passed to registerRecipe
   * @return Never returns - throws runtime exception with context
   */
  @Recover
  public Recipe recoverRegisterRecipe(Exception e, Recipe newRecipe) {
    String errorMsg =
        String.format("Failed to register recipe '%s' after 5 retries", newRecipe.getRecipeName());
    log.error(errorMsg, e);
    throw new RuntimeException(errorMsg, e);
  }

  /**
   * Helper method to create consolidated recipe (separates logic for readability)
   *
   * @param newRecipe Input recipe from request
   * @return Consolidated recipe with proper display name handling
   */
  private Recipe createConsolidatedRecipe(Recipe newRecipe) {
    if (StringUtils.isNotBlank(newRecipe.getDisplayName())
        && !newRecipe.getDisplayName().equals(newRecipe.getRecipeName())) {
      return new Recipe(newRecipe.getDisplayName());
    }
    return new Recipe(newRecipe.getRecipeName());
  }

  // -------------------------------------------------------------------------
  // Read Operations (non-transactional - no locks needed for read-only access)
  // -------------------------------------------------------------------------

  /**
   * Retrieves a recipe by name (read-only - no transaction needed)
   *
   * @param recipeName Name of the recipe to retrieve
   * @return Optional containing recipe if found, empty otherwise
   */
  public Optional<Recipe> getRecipeByName(String recipeName) {
    return repository.findById(recipeName);
  }

  /**
   * Retrieves all recipes (read-only - no transaction needed)
   *
   * @return Iterable of all recipes in the database
   */
  public Iterable<Recipe> getAllRecipes() {
    return repository.findAll();
  }

  // -------------------------------------------------------------------------
  // Write Operations (transactional with retry - deadlock-prone operations)
  // All write operations use:
  // 1. Propagation.REQUIRES_NEW (new transaction per retry)
  // 2. Rollback on all exceptions
  // 3. Exponential backoff retries (3 attempts - balanced for updates)
  // -------------------------------------------------------------------------

  /**
   * Updates recipe seasonality (string list input) with retry/transaction safeguards
   *
   * @param recipeName Name of recipe to update
   * @param seasonality List of season strings (will be converted to Season enum)
   * @return Optional containing updated recipe if found, empty otherwise
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3, // Fewer retries for updates (balance latency/retries)
      backoff = @Backoff(delay = 100, multiplier = 2) // 100ms → 200ms → 400ms
      )
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public Optional<Recipe> updateSeasonality(String recipeName, List<String> seasonality) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);

    // Null safety: Prevent NPE if seasonality list is null
    if (targetRecipe.isPresent() && !CollectionUtils.isEmpty(seasonality)) {
      List<Recipe.Season> seasonalityEnum =
          seasonality.stream().map(Recipe.Season::valueOf).collect(Collectors.toList());

      targetRecipe.get().getSeasonality().clear();
      targetRecipe.get().getSeasonality().addAll(seasonalityEnum);
      log.debug("Updated seasonality for recipe: {}", recipeName);
    }

    return targetRecipe;
  }

  /**
   * Overloaded method - Updates recipe seasonality (Set<Season> input)
   *
   * @param recipeName Name of recipe to update
   * @param seasonality Set of Season enum values
   * @return Optional containing updated recipe if found, empty otherwise
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public Optional<Recipe> updateSeasonality(String recipeName, Set<Recipe.Season> seasonality) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);

    if (targetRecipe.isPresent() && !CollectionUtils.isEmpty(seasonality)) {
      targetRecipe.get().getSeasonality().clear();
      targetRecipe.get().getSeasonality().addAll(seasonality);
      log.debug("Updated seasonality (enum set) for recipe: {}", recipeName);
    }

    return targetRecipe;
  }

  /**
   * Adds an ingredient to a recipe with sorted insert (deadlock prevention)
   *
   * @param recipeName Name of recipe to update
   * @param recipeIngredient Ingredient to add
   * @return Optional containing updated recipe if found, empty otherwise
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public Optional<Recipe> addIngredient(
      @NonNull String recipeName, @NonNull RecipeIngredient recipeIngredient) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);

    if (targetRecipe.isPresent()) {
      // Check for duplicate ingredient (avoid redundant inserts)
      boolean ingredientExists =
          targetRecipe.get().getIngredients().stream()
              .anyMatch(
                  existing ->
                      existing.getIngredientName().equals(recipeIngredient.getIngredientName()));

      if (!ingredientExists) {
        targetRecipe.get().getIngredients().add(recipeIngredient);
        // Insert ingredient (safe - single insert, no deadlock risk)
        ingredientRepository.insertIfNotExists(
            recipeIngredient.getIngredientName(), recipeIngredient.getDisplayName());
        log.debug(
            "Added ingredient {} to recipe {}", recipeIngredient.getIngredientName(), recipeName);
      }
    }

    return repository.findById(recipeName);
  }

  /**
   * Updates recipe ingredients with sorted inserts (critical deadlock prevention)
   *
   * @param recipeName Name of recipe to update
   * @param ingredients Set of ingredients to set for the recipe
   * @return Optional containing updated recipe if found, empty otherwise
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public Optional<Recipe> updateIngredients(String recipeName, Set<RecipeIngredient> ingredients) {
    Optional<Recipe> targetRecipe = getRecipeByName(recipeName);

    if (targetRecipe.isPresent() && !CollectionUtils.isEmpty(ingredients)) {
      // Critical: Sort ingredients to prevent circular deadlocks
      List<RecipeIngredient> sortedIngredients =
          ingredients.stream()
              .sorted(Comparator.comparing(RecipeIngredient::getIngredientName))
              .collect(Collectors.toList());

      // Insert ingredients in sorted order (consistent lock acquisition)
      sortedIngredients.forEach(
          ingredient -> {
            ingredientRepository.insertIfNotExists(
                ingredient.getIngredientName(), ingredient.getDisplayName());
          });

      // Update recipe ingredients only if changed (avoid unnecessary writes)
      if (!targetRecipe.get().getIngredients().equals(ingredients)) {
        targetRecipe.get().getIngredients().clear();
        targetRecipe.get().getIngredients().addAll(ingredients);
        log.debug("Updated ingredients for recipe: {}", recipeName);
      }
    }

    return repository.findById(recipeName);
  }

  /**
   * Updates recipe meal type with retry/transaction safeguards
   *
   * @param recipeName Name of recipe to update
   * @param mealType Set of MealType enum values to set
   * @return Optional containing updated recipe if found, empty otherwise
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public Optional<Recipe> updateMealType(String recipeName, Set<Recipe.MealType> mealType) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);

    if (targetRecipe.isPresent() && !CollectionUtils.isEmpty(mealType)) {
      targetRecipe.get().getMealType().clear();
      targetRecipe.get().getMealType().addAll(mealType);
      log.debug("Updated meal type for recipe: {}", recipeName);
    }

    return targetRecipe;
  }

  /**
   * Updates recipe image (uploads to MinIO) with retry/transaction safeguards
   *
   * @param recipeName Name of recipe to update
   * @param imageData InputStream of image content
   * @param size Size of image in bytes
   * @param contentType MIME type of image (e.g., image/jpeg)
   * @return Optional containing updated recipe if found, empty otherwise
   */
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public Optional<Recipe> updateImage(
      String recipeName, InputStream imageData, long size, String contentType) {
    Optional<Recipe> targetRecipe = repository.findById(recipeName);

    if (targetRecipe.isPresent() && imageData != null) {
      String key = "recipes/" + recipeName + getExtension(contentType);
      // Upload to MinIO (idempotent operation - safe to retry)
      storageService.upload(recipeBucket, key, imageData, size, contentType);
      targetRecipe.get().setImageUrl(key);
      log.debug("Updated image for recipe {} (MinIO key: {})", recipeName, key);
    }

    return targetRecipe;
  }

  /**
   * Helper method to get file extension from content type
   *
   * @param contentType MIME type of image
   * @return File extension (e.g., .jpg for image/jpeg)
   */
  private String getExtension(String contentType) {
    if (contentType == null) return "";

    return switch (contentType) {
      case "image/jpeg" -> ".jpg";
      case "image/png" -> ".png";
      case "image/gif" -> ".gif";
      case "image/webp" -> ".webp";
      default -> "";
    };
  }
}
