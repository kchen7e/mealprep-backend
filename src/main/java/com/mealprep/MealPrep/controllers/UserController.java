package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.user.UserWithAuthDTO;
import com.mealprep.MealPrep.entities.user.User;
import com.mealprep.MealPrep.service.UserService;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping(
      value = "/register",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<User>> registerUser(@RequestBody UserWithAuthDTO userWithAuthDTO) {
    if (StringUtils.isNotBlank(userWithAuthDTO.getPassword())) {
      Optional<UserWithAuthDTO> result = userService.registerUser(userWithAuthDTO);

      if (result.isPresent()) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Authorization", "Bearer " + result.get().getToken())
            .body(userService.getUserByUserName(userWithAuthDTO.getUserName()));
      }
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Optional.empty());
  }

  @PostMapping(
      value = "/get",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<User>> getUser(@RequestBody UserWithAuthDTO userWithAuthDTO) {
    Optional<UserWithAuthDTO> result = userService.authenticateUser(userWithAuthDTO);
    if (result.isPresent()) {
      return ResponseEntity.status(HttpStatus.ACCEPTED)
          .header("Authorization", "Bearer " + result.get().getToken())
          .body(userService.getUserByUserName(result.get().getUserName()));
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
  }

  @PatchMapping(
      value = "/update",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<User>> updateUser(@RequestBody UserWithAuthDTO userWithAuthDTO) {
    Optional<User> result = userService.updateUser(userWithAuthDTO);
    if (result.isPresent()) {
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }

    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(Optional.empty());
  }

  @PostMapping(
      value = "/logout",
      consumes = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Optional<User>> logOutUser(@RequestBody UserWithAuthDTO userWithAuthDTO) {
    userService.logoutUser(userWithAuthDTO);
    return ResponseEntity.status(HttpStatus.OK).body(Optional.empty());
  }
}
