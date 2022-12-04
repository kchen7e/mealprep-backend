package com.mealprep.MealPrep.service;

import com.mealprep.MealPrep.database.UserCredentialsRepository;
import com.mealprep.MealPrep.database.UserRepository;
import com.mealprep.MealPrep.entities.api.user.UserWithAuthDTO;
import com.mealprep.MealPrep.entities.user.User;
import com.mealprep.MealPrep.entities.user.Token;
import com.mealprep.MealPrep.entities.user.UserCredentials;
import com.sun.tools.jconsole.JConsoleContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserCredentialsRepository credentialsRepository;

    @Autowired
    private Argon2PasswordEncoder passwordEncoder;

//    @Transactional
//    public User registerUser(String userName) {
//        User newUser = new User(userName);
//        repository.save(newUser);
//        return repository.findById(userName).get();
//    }


    @Transactional
    public Optional<UserWithAuthDTO> registerUser(UserWithAuthDTO userWithAuthDTO) {
        if (StringUtils.isNotBlank(
                userWithAuthDTO.getPassword()) && getUserByUserName(
                userWithAuthDTO.getUserName()).isEmpty()) {
            User newUser = new User(userWithAuthDTO.getUserName());
            newUser.setEmail(userWithAuthDTO.getEmail());
            newUser.setFirstName(userWithAuthDTO.getFirstName());
            newUser.setLastName(userWithAuthDTO.getLastName());
            newUser.setCountry(userWithAuthDTO.getCountry());
            UserCredentials newUserCredentials = new UserCredentials(
                    newUser.getUserName(),
                    passwordEncoder.encode(userWithAuthDTO.getPassword()));
            Token newUserToken = new Token(
                    RandomStringUtils.randomAlphabetic(10));
            newUserCredentials.addToken(newUserToken);
            credentialsRepository.save(newUserCredentials);
            userWithAuthDTO.setToken(newUserToken.getToken());
            repository.save(newUser);
            return Optional.of(userWithAuthDTO);
        } else {
            return Optional.empty();
        }
    }


    @Transactional
    public Optional<UserWithAuthDTO> authenticateUser(UserWithAuthDTO userWithAuthDTO) {
        if (StringUtils.isNotBlank(userWithAuthDTO.getPassword()) || StringUtils.isNotBlank(
                userWithAuthDTO.getToken())) {
            Optional<User> userInDB = getUserByUserName(
                    userWithAuthDTO.getUserName());
            if (userInDB.isPresent()) {
                Optional<UserCredentials> userCredentials = credentialsRepository.findById(
                        userInDB.get().getUserName());
                if (userCredentials.isPresent()) {
                    //if a valid token is present then pass back info
                    if (StringUtils.isNotBlank(userWithAuthDTO.getToken())) {
                        if (userCredentials.get().validateToken(userWithAuthDTO.getToken())) {
                            return Optional.of(userWithAuthDTO);
                        }
                    }
                    // no valid token, check for master password match
                    if (StringUtils.isNotBlank(userWithAuthDTO.getPassword())) {
                        if (passwordEncoder.matches(userWithAuthDTO.getPassword(),
                                                    userCredentials.get().getPassword())) {
                            //password is okay, get a new token
                            Token newUserToken = new Token(RandomStringUtils.randomAlphabetic(10));
                            userCredentials.get().addToken(newUserToken);
                            userWithAuthDTO.setToken(newUserToken.getToken());
                            return Optional.of(userWithAuthDTO);
                        } else {
                            //a valid user without valid token or password
                            return Optional.empty();
                        }
                    }
                }
            }
        }
        // no token, no password
        return Optional.empty();
    }


    public Optional<User> validateToken(UserWithAuthDTO userWithAuthDTO) {
        Optional<User> userInDB = repository.findById(userWithAuthDTO.getUserName());
        if (userInDB.isPresent()) {
            Optional<UserCredentials> userCredentials =
                    credentialsRepository.findById(
                    userWithAuthDTO.getUserName());
            if (userCredentials.isPresent() && userCredentials.get().validateToken(
                    userWithAuthDTO.getToken())) {
                return userInDB;
            }
        }
        return Optional.empty();
    }


    @Transactional
    public Optional<User> addTokenToUser(UserWithAuthDTO userWithAuthDTO,
                                    Token token) {
        Optional<User> userInDB = repository.findById(userWithAuthDTO.getUserName());
        if (userInDB.isPresent()) {
            Optional<UserCredentials> credentials = credentialsRepository.findById(
                    userWithAuthDTO.getUserName());
            credentials.ifPresent(
                    userCredentials -> userCredentials.addToken(token));
                return userInDB;
        }
        return Optional.empty();
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
            Optional<User> targetUser =
                    repository.findById(result.get().getUserName());
            if (targetUser.isPresent()) {
                if (StringUtils.isNotBlank(userWithAuthDTO.getEmail())){
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
                if (StringUtils.isNotBlank(userWithAuthDTO.getNewPassword())) {
                    Optional<UserCredentials> targetUserCredentials =
                            credentialsRepository.findById(targetUser.get().getUserName());
                    if (targetUserCredentials.isPresent()) {
                        targetUserCredentials.get().setPassword(passwordEncoder.encode(result.get().getNewPassword()));
                    } else {
                        System.err.println("User " + result.get().getUserName() + "doesn't have credentials");
                        credentialsRepository.save(new UserCredentials(result.get().getUserName(),
                                                                       passwordEncoder.encode(
                                                                               result.get().getNewPassword())));
                    }
                }
                return targetUser;
            }
        }
        return Optional.empty();
    }







}
