package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.config.JwtUtil;
import com.mealprep.MealPrep.database.UserCredentialsRepository;
import com.mealprep.MealPrep.database.UserRepository;
import com.mealprep.MealPrep.entities.api.user.UserWithAuthDTO;
import com.mealprep.MealPrep.entities.user.User;
import com.mealprep.MealPrep.entities.user.UserCredentials;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository repository;
  private final UserCredentialsRepository credentialsRepository;
  private final Argon2PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public UserService(
      UserRepository repository,
      UserCredentialsRepository credentialsRepository,
      Argon2PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil) {
    this.repository = repository;
    this.credentialsRepository = credentialsRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  //    @Transactional
  //    public User registerUser(String userName) {
  //        User newUser = new User(userName);
  //        repository.save(newUser);
  //        return repository.findById(userName).get();
  //    }

  @Transactional
  public Optional<UserWithAuthDTO> registerUser(UserWithAuthDTO userWithAuthDTO) {
    if (StringUtils.isNotBlank(userWithAuthDTO.getPassword())
        && getUserByUserName(userWithAuthDTO.getUserName()).isEmpty()) {
      User newUser = new User(userWithAuthDTO.getUserName());
      newUser.setEmail(userWithAuthDTO.getEmail());
      newUser.setFirstName(userWithAuthDTO.getFirstName());
      newUser.setLastName(userWithAuthDTO.getLastName());
      newUser.setCountry(userWithAuthDTO.getCountry());
      UserCredentials newUserCredentials =
          new UserCredentials(
              newUser.getUserName(), passwordEncoder.encode(userWithAuthDTO.getPassword()));
      credentialsRepository.save(newUserCredentials);
      repository.save(newUser);
      // Generate JWT token
      String jwtToken = jwtUtil.generateToken(newUser.getUserName());
      userWithAuthDTO.setToken(jwtToken);
      return Optional.of(userWithAuthDTO);
    } else {
      return Optional.empty();
    }
  }

  @Transactional
  public Optional<UserWithAuthDTO> authenticateUser(UserWithAuthDTO userWithAuthDTO) {
    // JWT token validation - if valid JWT provided, authenticate
    if (StringUtils.isNotBlank(userWithAuthDTO.getToken())) {
      if (jwtUtil.validateToken(userWithAuthDTO.getToken())) {
        String username = jwtUtil.extractUsername(userWithAuthDTO.getToken());
        userWithAuthDTO.setUserName(username);
        return Optional.of(userWithAuthDTO);
      }
    }

    // Password-based authentication
    if (StringUtils.isNotBlank(userWithAuthDTO.getPassword())
        && StringUtils.isNotBlank(userWithAuthDTO.getUserName())) {
      Optional<User> userInDB = getUserByUserName(userWithAuthDTO.getUserName());
      if (userInDB.isPresent()) {
        Optional<UserCredentials> userCredentials =
            credentialsRepository.findById(userInDB.get().getUserName());
        if (userCredentials.isPresent()
            && passwordEncoder.matches(
                userWithAuthDTO.getPassword(), userCredentials.get().getPassword())) {
          // Password valid - generate JWT
          String jwtToken = jwtUtil.generateToken(userInDB.get().getUserName());
          userWithAuthDTO.setToken(jwtToken);
          return Optional.of(userWithAuthDTO);
        }
      }
    }

    return Optional.empty();
  }

  public Optional<User> validateToken(String token) {
    if (StringUtils.isBlank(token) || !jwtUtil.validateToken(token)) {
      return Optional.empty();
    }
    String username = jwtUtil.extractUsername(token);
    return repository.findById(username);
  }

  public boolean logoutUser(UserWithAuthDTO userWithAuthDTO) {
    // JWT is stateless - logout is handled client-side by deleting the token
    // This method exists for API compatibility
    return true;
  }

  public Optional<User> getUserByUserName(String userName) {
    return repository.findById(userName);
  }

  public Optional<User> getUserByUserEmail(String email) {
    return repository.findByEmail(email);
  }

  @Transactional
  public Optional<User> updateUser(UserWithAuthDTO userWithAuthDTO) {
    Optional<UserWithAuthDTO> result = authenticateUser(userWithAuthDTO);
    System.out.println(userWithAuthDTO.getUserName());
    System.out.println(userWithAuthDTO.getFirstName());
    System.out.println(userWithAuthDTO.getCountry());
    if (result.isPresent()) {
      Optional<User> targetUser = repository.findById(result.get().getUserName());
      if (targetUser.isPresent()) {
        if (StringUtils.isNotBlank(userWithAuthDTO.getEmail())) {
          targetUser.get().setEmail(userWithAuthDTO.getEmail());
        }
        if (StringUtils.isNotBlank(userWithAuthDTO.getFirstName())) {
          targetUser.get().setFirstName(userWithAuthDTO.getFirstName());
        }
        if (StringUtils.isNotBlank(userWithAuthDTO.getLastName())) {
          targetUser.get().setLastName(userWithAuthDTO.getLastName());
        }
        if (StringUtils.isNotBlank(userWithAuthDTO.getCountry())) {
          targetUser.get().setCountry(userWithAuthDTO.getCountry());
        }
        if (StringUtils.isNotBlank(userWithAuthDTO.getPassword())) {
          Optional<UserCredentials> targetUserCredentials =
              credentialsRepository.findById(targetUser.get().getUserName());
          if (targetUserCredentials.isPresent()) {
            targetUserCredentials
                .get()
                .setPassword(passwordEncoder.encode(userWithAuthDTO.getPassword()));
          } else {
            System.err.println("User " + result.get().getUserName() + "doesn't have credentials");
            credentialsRepository.save(
                new UserCredentials(
                    result.get().getUserName(),
                    passwordEncoder.encode(userWithAuthDTO.getPassword())));
          }
        }
        return targetUser;
      }
    }
    return Optional.empty();
  }
}
