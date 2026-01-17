package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.user.UserCredentials;
import org.springframework.data.repository.CrudRepository;

public interface UserCredentialsRepository extends CrudRepository<UserCredentials, String> {}
