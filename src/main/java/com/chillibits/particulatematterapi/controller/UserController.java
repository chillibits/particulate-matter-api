/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "User REST Endpoint", tags = "user")
public class UserController {
    UserRepository userRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all users, registered in the database")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns details for one specific user")
    public User getUserById(@PathVariable("id") Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an user to the database")
    public User addUser(@RequestBody User user) {
        // Check if user already exists
        if(userRepository.existsById(user.getId())) return null;
        // Add additional information to user object
        long currentTimestamp = System.currentTimeMillis();
        user.setCreationTimestamp(currentTimestamp);
        user.setLastEditTimestamp(currentTimestamp);
        user.setStatus(User.EMAIL_CONFIRMATION_PENDING);
        user.setRole(User.USER);
        return userRepository.save(user);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an existing user")
    public Integer updateUser(@RequestBody User user) {
        return userRepository.updateUser(user.getId(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getRole(), user.getStatus());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{id}")
    @ApiOperation(value = "Deletes an user from the database")
    public void deleteUser(@PathVariable("id") int id) {
        userRepository.deleteById(id);
    }
}