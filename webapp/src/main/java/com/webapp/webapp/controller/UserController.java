package com.webapp.webapp.controller;

import com.timgroup.statsd.StatsDClient;
import com.webapp.webapp.exception.BadRequestException;
import com.webapp.webapp.exception.ResourceNotFoundException;
import com.webapp.webapp.repository.model.User;
import com.webapp.webapp.model.UserCredentials;
import com.webapp.webapp.repository.UserRepository;
import com.webapp.webapp.security.AuthenticationProvider;
import com.webapp.webapp.security.PasswordEncoder;
import com.webapp.webapp.validator.UserDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/v1")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDataValidator userDataValidator;

    @Autowired
    StatsDClient statsDClient;

    //get all users in database
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //get user by id
    @GetMapping("/user/self")
    public ResponseEntity<User> getUserById(@RequestHeader("Authorization") String authHeader)
            throws ResourceNotFoundException {
        long currentApiExecution = System.currentTimeMillis();
        UserCredentials userCredentials = AuthenticationProvider.fetchCredentialsFromHeader(authHeader);
        logger.info("Getting User in Database with Email {}", userCredentials.getEmail());
        User user=userRepository.findByEmail(userCredentials.getEmail()).
                orElseThrow(()-> new ResourceNotFoundException("User not found " + userCredentials.getEmail()));
        PasswordEncoder.checkPassword(userCredentials.getPassword(), user.getPassword());
        user.setPassword(null);
        long endApiExecution = System.currentTimeMillis();
        statsDClient.increment("APIGetUserCount");
        statsDClient.recordExecutionTime("APIGetUserTime", endApiExecution-currentApiExecution);
        return ResponseEntity.ok().headers(formAuthHeader(userCredentials)).body(user);

    }

    //create user
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody User user){

        // Validation of user data.
        long currentApiExecution = System.currentTimeMillis();
        userDataValidator.checkEmail(user.getEmail());
        userDataValidator.checkUserData(user.getFirstName(), user.getLastName());

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser.isPresent()){
            logger.error("Email Already Exists {}", user.getEmail());
            throw new BadRequestException("Email already exists","email");
        }
        userDataValidator.checkPasswordStrength(user.getPassword());

        UserCredentials userCredentials = new UserCredentials(user.getEmail(), user.getPassword());

        user.setPassword(PasswordEncoder.encodePassword(user.getPassword()));
        logger.info("Creating User in Database with Email {}", user.getEmail());

        long databaseStart = System.currentTimeMillis();
        ResponseEntity<User> responseEntity =
                ResponseEntity.ok().headers(formAuthHeader(userCredentials))
                .body(userRepository.save(user));
        long databaseEnd = System.currentTimeMillis();
        logger.info("Create User in Database {}", user.getEmail());
        statsDClient.increment("APIUserCreateCount");
        long endApiExecution = System.currentTimeMillis();
        statsDClient.recordExecutionTime("APIUserCreateTime", endApiExecution-currentApiExecution);
        statsDClient.recordExecutionTime("DatabaseUserCreateTime", databaseEnd-databaseStart);
        return responseEntity;
    }



    @PutMapping("/user/self")
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String authHeader,
                                           @Validated @RequestBody User userDetails) throws ResourceNotFoundException {

        // validation of input data.
        long currentApiExecution = System.currentTimeMillis();
        userDataValidator.checkUserData(userDetails.getFirstName(), userDetails.getLastName());
        userDataValidator.checkPasswordStrength(userDetails.getPassword());

        UserCredentials credentials = AuthenticationProvider.fetchCredentialsFromHeader(authHeader);
        User user = userRepository.findByEmail(credentials.getEmail()).orElseThrow(()
                -> new ResourceNotFoundException("Resource not found " + credentials.getEmail()));
        PasswordEncoder.checkPassword(credentials.getPassword(), user.getPassword());
        user.setLastName(userDetails.getLastName());
        user.setFirstName(userDetails.getFirstName());
        user.setPassword(PasswordEncoder.encodePassword(userDetails.getPassword()));
        user.setUpdatedAt(new Date());
        long databaseStart = System.currentTimeMillis();
        final User updatedUser = userRepository.save(user);
        long databaseEnd = System.currentTimeMillis();
        UserCredentials updatedCredentials = new UserCredentials(user.getEmail(), userDetails.getPassword());
        updatedUser.setPassword(null);
        statsDClient.increment("APIUpdateUserCount");
        long endApiExecution = System.currentTimeMillis();
        statsDClient.recordExecutionTime("APIUserUpdateTime", endApiExecution-currentApiExecution);
        statsDClient.recordExecutionTime("DatabaseUserUpdateTime", databaseEnd-databaseStart);
        return ResponseEntity.ok().headers(formAuthHeader(updatedCredentials)).body(updatedUser);
    }

    private HttpHeaders formAuthHeader(UserCredentials userCredentials) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String encoding = Base64.getEncoder().encodeToString((userCredentials.getEmail()
                + ":" + userCredentials.getPassword()).getBytes(StandardCharsets.UTF_8));
        responseHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
        return responseHeaders;
    }

}
