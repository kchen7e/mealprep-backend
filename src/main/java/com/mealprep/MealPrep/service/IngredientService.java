package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.IngredientRepository;
import com.mealprep.MealPrep.entities.recipe.Ingredient;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {
  @Autowired private IngredientRepository repository;
  @Autowired private StorageService storageService;

  @Value("${minio.bucket.ingredient:ingredient-images}")
  private String ingredientBucket;

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
  public Optional<Ingredient> addDependencies(String ingredientName, Set<Ingredient> dependencies) {
    Optional<Ingredient> targetIngredient = getIngredientByName(ingredientName);
    targetIngredient.ifPresent(
        ingredient -> {
          dependencies.forEach(
              dependency -> {
                if (!ingredient.getIngredientName().equals(dependency.getIngredientName())
                    && repository.findById(dependency.getIngredientName()).isEmpty()) {
                  Ingredient newIngredient;
                  if (StringUtils.isNotBlank(dependency.getDisplayName())
                      && !dependency.getIngredientName().equals(dependency.getDisplayName())) {
                    newIngredient = registerIngredient(dependency.getDisplayName());
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
  public Optional<Ingredient> updateSeasonality(
      String ingredientName, Set<Ingredient.Season> seasonality) {
    Optional<Ingredient> targetIngredient = repository.findById(ingredientName);
    targetIngredient.ifPresent(
        ingredient -> {
          ingredient.getSeasonality().clear();
          ingredient.getSeasonality().addAll(seasonality);
        });
    return targetIngredient;
  }

  @Transactional
  public Optional<Ingredient> updateImage(
      String ingredientName, InputStream imageData, long size, String contentType) {
    Optional<Ingredient> targetIngredient = repository.findById(ingredientName);
    if (targetIngredient.isPresent()) {
      String key = "ingredients/" + ingredientName + getExtension(contentType);
      storageService.upload(ingredientBucket, key, imageData, size, contentType);
      targetIngredient.get().setImageUrl(key);
    }
    return targetIngredient;
  }

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
