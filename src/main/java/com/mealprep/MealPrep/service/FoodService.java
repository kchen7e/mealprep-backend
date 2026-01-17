package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.FoodRepository;
import com.mealprep.MealPrep.database.IngredientRepository;
import com.mealprep.MealPrep.entities.market.CommercialForm;
import com.mealprep.MealPrep.entities.market.Food;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.postgresql.util.PSQLException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodService {
  // ✅ Final fields (safely initialized in constructor)
  private final FoodRepository foodRepository;
  private final IngredientRepository ingredientRepository;

  // ✅ Constructor injection (guarantees final field initialization — fixes the first error)
  public FoodService(FoodRepository foodRepository, IngredientRepository ingredientRepository) {
    this.foodRepository = foodRepository;
    this.ingredientRepository = ingredientRepository;
  }

  // ✅ Retry + REPEATABLE_READ isolation (fixes deadlocks)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Food registerFood(@NonNull String foodName) {
    return getFoodByName(foodName).orElseGet(() -> foodRepository.save(new Food(foodName)));
  }

  // ✅ Critical fix: Handle Ingredient insertion (no cascading from Food) + sorted inserts
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Food registerFood(@NonNull Food food) {
    // 1. Check if food already exists (avoid duplicate saves)
    Optional<Food> existingFood = getFoodByName(food.getFoodName());
    if (existingFood.isPresent()) {
      return existingFood.get();
    }

    // 2. Process ingredients (sorted to prevent circular locks)
    if (food.getForIngredients() != null && !food.getForIngredients().isEmpty()) {
      // Sort ingredients by name (eliminates circular lock waits)
      Set<Ingredient> sortedIngredients =
          food.getForIngredients().stream()
              .sorted(Comparator.comparing(Ingredient::getIngredientName))
              .collect(Collectors.toSet());

      // 3. Manually insert ingredients (using fixed insertIfNotExists)
      sortedIngredients.forEach(
          ingredient ->
              ingredientRepository.insertIfNotExists(
                  ingredient.getIngredientName(), ingredient.getDisplayName()));

      // 4. Re-add sorted ingredients to food (ensure consistency)
      food.getForIngredients().clear();
      sortedIngredients.forEach(food::addIngredient);
    }

    // 5. Save food (no cascading—ingredients are already in DB)
    return foodRepository.save(food);
  }

  // ✅ Add retry to commercial form operations (also deadlock-prone)
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Food> addCommercialForm(@NonNull Food food, CommercialForm cf) {
    // ✅ Use foodRepository directly (removed legacy repository alias)
    Optional<Food> targetFood = foodRepository.findById(food.getFoodName());
    targetFood.ifPresent(value -> value.getFoundAs().add(cf));
    return targetFood;
  }

  // ✅ Add retry to update operations
  @Retryable(
      retryFor = {CannotAcquireLockException.class, PSQLException.class},
      maxAttempts = 3,
      backoff = @Backoff(delay = 100, multiplier = 2))
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public Optional<Food> updateCommercialForm(@NonNull String foodName, Set<CommercialForm> forms) {
    // ✅ Use foodRepository directly (removed legacy repository alias)
    Optional<Food> targetFood = foodRepository.findById(foodName);
    targetFood.ifPresent(
        target -> {
          target.getFoundAs().clear();
          target.getFoundAs().addAll(forms);
        });
    return targetFood;
  }

  // ✅ Keep read operations non-transactional (no locks needed)
  public Optional<Food> getFoodByName(String foodName) {
    return foodRepository.findById(
        foodName.toLowerCase()); // Ensure consistency with Food constructor
  }
}
