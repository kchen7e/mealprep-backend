package com.mealprep.MealPrep.controllers;

import com.mealprep.MealPrep.entities.api.user.UserWithAuthDTO;
import com.mealprep.MealPrep.entities.user.User;
import com.mealprep.MealPrep.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.weaver.patterns.IToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@ResponseBody
public class UserController {
    @Autowired
    UserService userService;


    @PostMapping(value = "/register", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Optional<User>> registerUser(@RequestBody UserWithAuthDTO userWithAuthDTO) {
        if (StringUtils.isNotBlank(userWithAuthDTO.getPassword())) {
            Optional<UserWithAuthDTO> result = userService.registerUser(userWithAuthDTO);

            if (result.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).header(
                        "Access-Control-Expose-Headers", "Authorization").
                        header("Authorization", result.get().getToken()).
                                             body(userService.getUserByUserName(userWithAuthDTO.getUserName()));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Optional.empty());
    }



    @PostMapping(value = "/get", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Optional<User>> getUser(@RequestBody UserWithAuthDTO userWithAuthDTO) {
        Optional<UserWithAuthDTO> result = userService.authenticateUser(userWithAuthDTO);
        if (result.isPresent()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).
                    header("Access-Control-Expose-Headers", "Authorization").
                    header("Authorization",result.get().getToken()).
                                  body(userService.getUserByUserName(result.get().getUserName()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
    }

    @PatchMapping(value = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Optional<User>> updateUser(
            @RequestBody UserWithAuthDTO userWithAuthDTO) {
        Optional<User> result = userService.updateUser(
                userWithAuthDTO);
            if (result.isPresent()) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).
                        body(result);
            }

        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(Optional.empty());
    }

    @PostMapping(value = "/logout", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Optional<User>> logOutUser(
    @RequestBody UserWithAuthDTO userWithAuthDTO) {
        System.out.printf("user is is %s%n", userWithAuthDTO);
//        String token = userWithAuthDTO.getToken();
//        System.out.printf("token is %s%n", token);
        return ResponseEntity.status(HttpStatus.OK).body(
                Optional.empty());
    }
}
