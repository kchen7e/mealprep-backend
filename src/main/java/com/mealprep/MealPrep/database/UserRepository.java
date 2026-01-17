package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.user.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
  Optional<User> findByEmail(String email);
}
