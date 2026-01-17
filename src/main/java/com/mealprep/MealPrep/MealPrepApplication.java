package com.mealprep.MealPrep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Main application class for MealPrep backend. @EnableRetry activates Spring's retry functionality
 * for @Retryable annotated methods.
 */
@SpringBootApplication
@EnableRetry // Enables retry logic globally (critical for deadlock handling)
public class MealPrepApplication {

  public static void main(String[] args) {
    SpringApplication.run(MealPrepApplication.class, args);
  }
}
