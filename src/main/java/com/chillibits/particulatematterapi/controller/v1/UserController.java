/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.UserDataException;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.UserDto;
import com.chillibits.particulatematterapi.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "User REST Endpoint", tags = "user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper mapper;

    @RequestMapping(method = RequestMethod.GET, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all users, registered in the database", hidden = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns details for one specific user")
    public UserDto getUserByEmail(@PathVariable("email") String email) {
        if(email == null) return null;
        return convertToDto(userRepository.findByEmail(email));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE, params = "password")
    @ApiOperation(value = "Returns details for one specific user after checking login credentials")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "This user does not exist"),
            @ApiResponse(code = 201, message = "The user exists, but the provided password is wrong")
    })
    public UserDto login(@PathVariable("email") String email, @RequestParam("password") String password) throws UserDataException {
        if(email == null || password == null) return null;
        User user = userRepository.findByEmail(email);
        if(user == null) throw new UserDataException(ErrorCodeUtils.USER_NOT_EXISTING);
        if(!user.getPassword().equals(password)) throw new UserDataException(ErrorCodeUtils.PASSWORD_WRONG);
        return convertToDto(user);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an user to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Please provide an user object with all fields filled"),
            @ApiResponse(code = 201, message = "This user already exists")
    })
    public User addUser(@RequestBody User user) throws UserDataException {
        // Validity checks
        if(userRepository.findByEmail(user.getEmail()) != null) throw new UserDataException(ErrorCodeUtils.USER_ALREADY_EXISTS);
        validateUserObject(user);
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
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Please provide an user object with all fields filled"),
            @ApiResponse(code = 201, message = "This user does not exist")
    })
    public Integer updateUser(@RequestBody User user) throws UserDataException {
        // Validity checks
        if(userRepository.findByEmail(user.getEmail()) == null) throw new UserDataException(ErrorCodeUtils.USER_NOT_EXISTING);
        validateUserObject(user);
        return userRepository.updateUser(user.getId(), user.getFirstName(), user.getLastName(), user.getPassword(), user.getRole(), user.getStatus());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{id}")
    @ApiOperation(value = "Deletes an user from the database")
    public void deleteUser(@PathVariable("id") int id) {
        userRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private void validateUserObject(User user) throws UserDataException {
        if(user.getEmail().isBlank() || user.getPassword().isBlank()) throw new UserDataException(ErrorCodeUtils.INVALID_USER_DATA);
    }

    private UserDto convertToDto(User sensor) {
        return mapper.map(sensor, UserDto.class);
    }
}