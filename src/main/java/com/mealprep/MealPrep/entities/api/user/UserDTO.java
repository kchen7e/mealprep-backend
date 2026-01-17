package com.mealprep.MealPrep.entities.api.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class UserDTO {
  @Getter private String userName;

  public UserDTO(@NonNull String userName) {
    this.userName = userName.toLowerCase();
  }
}
