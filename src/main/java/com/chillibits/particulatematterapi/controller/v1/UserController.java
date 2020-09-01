/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.dto.UserDto;
import com.chillibits.particulatematterapi.model.dto.UserInsertUpdateDto;
import com.chillibits.particulatematterapi.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "User REST Endpoint", tags = "user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all users, registered in the database", hidden = true)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns details for one specific user")
    public UserDto getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE, params = "password")
    @ApiOperation(value = "Returns details for one specific user after checking login credentials")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "This user does not exist"),
            @ApiResponse(code = 406, message = "The user exists, but the provided password is wrong")
    })
    public UserDto signIn(@PathVariable("email") String email, @RequestParam("password") String password) {
        return userService.checkUserDataAndSignIn(email, password);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an user to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide an user object with all fields filled"),
            @ApiResponse(code = 406, message = "This user already exists")
    })
    public UserDto addUser(@RequestBody UserInsertUpdateDto user) {
        return userService.addUser(user);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/confirm/{confirmationToken}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Confirms an user account", hidden = true)
    public String confirmAccount(@PathVariable String confirmationToken) {
        return "redirect:https://www.chillibits.com/pmapp?p=confirmation&param=" + (userService.confirmAccount(confirmationToken) ? "success" : "failure");
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an existing user")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide an user object with all fields filled"),
            @ApiResponse(code = 406, message = "This user does not exist")
    })
    public Integer updateUser(@RequestBody UserInsertUpdateDto user) {
        return userService.updateUser(user);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{id}")
    @ApiOperation(value = "Deletes an user from the database")
    public void deleteUser(@PathVariable("id") int id) {
        userService.deleteUserById(id);
    }
}