package com.mealprep.MealPrep.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    // Match /api/recipe/get/all and /api/recipe/get/all/ the same way
    configurer.setUseTrailingSlashMatch(true);
  }
}
