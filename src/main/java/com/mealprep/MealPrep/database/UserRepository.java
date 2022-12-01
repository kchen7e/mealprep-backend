package com.mealprep.MealPrep.database;

import com.mealprep.MealPrep.entities.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, String> {
        Optional<User> findByEmail(String email);
}
