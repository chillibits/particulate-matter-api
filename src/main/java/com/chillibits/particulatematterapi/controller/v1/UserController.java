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

/**
 * User endpoint
 *
 * Endpoint for managing user accounts of the API
 */
@RestController
@Api(value = "User REST Endpoint", tags = "user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Returns all users, registered in the database
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @return List of users as List of UserDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all users, registered in the database", hidden = true)
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Returns details for one specific user
     * <p>Note: Requires at least application role A (usual application)</p>
     *
     * @param email Email of the requested user
     * @return User as UserDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns details for one specific user")
    public UserDto getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Returns details for one specific user after checking login credentials
     * <p>Note: Requires at least application role A (usual application)</p>
     *
     * @param email Email of the requested user
     * @param password Password of the requested user (Hashed with SHA-256)
     * @return User as UserDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/user/{email}", produces = MediaType.APPLICATION_JSON_VALUE, params = "password")
    @ApiOperation(value = "Returns details for one specific user after checking login credentials")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "This user does not exist"),
            @ApiResponse(code = 406, message = "The user exists, but the provided password is wrong")
    })
    public UserDto signIn(@PathVariable("email") String email, @RequestParam("password") String password) {
        return userService.checkUserDataAndSignIn(email, password);
    }

    /**
     * Adds an user to the database
     * <p>Note: Requires at least application role CBA (ChilliBits application)</p>
     *
     * @param user Instance of UserInsertUpdateDto with all required data values
     * @return Inserted user record as UserDto
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds an user to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide an user object with all fields filled"),
            @ApiResponse(code = 406, message = "This user already exists")
    })
    public UserDto addUser(@RequestBody UserInsertUpdateDto user) {
        return userService.addUser(user);
    }

    /**
     * Confirms an user account
     *
     * @param confirmationToken Randomly generated confirmation token, which is included in confirmation links
     * @return Redirect to the pmapp website to show a success / failure message
     */
    @RequestMapping(method = RequestMethod.GET, path = "/user/confirm/{confirmationToken}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Confirms an user account", hidden = true)
    public String confirmAccount(@PathVariable String confirmationToken) {
        return "redirect:https://www.chillibits.com/pmapp?p=confirmation&param=" + (userService.confirmAccount(confirmationToken) ? "success" : "failure");
    }

    /**
     * Updates an existing user
     * <p>Note: Requires at least application role CBA (ChilliBits application)</p>
     *
     * @param user Instance of UserInsertUpdateDto with all required data values
     * @return Status code of the update transaction
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates an existing user")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide an user object with all fields filled"),
            @ApiResponse(code = 406, message = "This user does not exist")
    })
    public Integer updateUser(@RequestBody UserInsertUpdateDto user) {
        return userService.updateUser(user);
    }

    /**
     * Deletes an user from the database
     * <p>Note: Requires at least application role CBA (ChilliBits application)</p>
     *
     * @param id Id of the user, which has to be deleted
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{id}")
    @ApiOperation(value = "Deletes an user from the database")
    public void deleteUser(@PathVariable("id") int id) {
        userService.deleteUserById(id);
    }
}