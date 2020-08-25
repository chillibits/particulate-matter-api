/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.UserDataException;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.UserDto;
import com.chillibits.particulatematterapi.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("User Controller")
public class UserControllerTests {

    @Autowired
    private UserController userController;
    @MockBean
    private UserRepository userRepository;

    private final List<User> testData = getTestData();
    private final List<UserDto> assData = getAssertData();

    @TestConfiguration
    static class SensorControllerImplTestContextConfiguration {

        @Bean
        public UserController userController() {
            return new UserController();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        Mockito.when(userRepository.findAll()).thenReturn(testData);
        Mockito.when(userRepository.findByEmail(testData.get(0).getEmail())).thenReturn(testData.get(0));
        Mockito.when(userRepository.save(any(User.class))).then(returnsFirstArg());
        Mockito.when(userRepository.updateUser(anyInt(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(1);
        Mockito.doNothing().when(userRepository).deleteById(anyInt());
    }

    // -------------------------------------------------- Get users ----------------------------------------------------

    @Test
    @DisplayName("Test for getting all users successfully")
    public void testGetUsers() {
        // Get all sensors
        List<UserDto> result = userController.getAllUsers();
        assertThat(result).containsExactlyInAnyOrder(assData.toArray(UserDto[]::new));
    }

    @Test
    @DisplayName("Test for getting an user by its email")
    public void testGetUserByEmail() {
        UserDto result = userController.getUserByEmail(testData.get(0).getEmail());
        assertEquals(assData.get(0), result);
    }

    @Test
    @DisplayName("Test for getting an user by its email with invalid email")
    public void testGetUserByEmailInvalidEmail() {
        UserDto result = userController.getUserByEmail(null);
        assertThat(result).isNull();
    }

    // -------------------------------------------------- Add user -----------------------------------------------------

    @Test
    @DisplayName("Test for adding an user successfully")
    public void testAddUser() throws UserDataException {
        User result = userController.addUser(testData.get(1));
        assertEquals(testData.get(1), result);
    }

    @Test
    @DisplayName("Test for adding an user, triggering a UserAlreadyExists exception")
    public void testAddUserExceptionUserAlreadyExists() {
        // Try with invalid input
        Exception exception = assertThrows(UserDataException.class, () ->
                userController.addUser(testData.get(0))
        );

        String expectedMessage = new UserDataException(ErrorCodeUtils.USER_ALREADY_EXISTS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for adding an user, triggering a InvalidUserData exception")
    public void testAddUserExceptionInvalidUserData() {
        // Try with invalid input
        Exception exception = assertThrows(UserDataException.class, () ->
                userController.addUser(testData.get(2))
        );

        String expectedMessage = new UserDataException(ErrorCodeUtils.INVALID_USER_DATA).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ------------------------------------------------ Update users ---------------------------------------------------

    @Test
    @DisplayName("Test for updating an user successfully")
    public void testUpdateUser() throws UserDataException {
        int result = userController.updateUser(testData.get(0));
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Test for updating an user, triggering a InvalidUserData exception")
    public void testUpdateUserExceptionInvalidUserData() {
        // Try with invalid input
        Exception exception = assertThrows(UserDataException.class, () ->
                userController.updateUser(testData.get(2))
        );

        String expectedMessage = new UserDataException(ErrorCodeUtils.INVALID_USER_DATA).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ------------------------------------------------ Delete users ---------------------------------------------------

    @Test
    @DisplayName("Test for deleting an user")
    public void testDeleteUser() {
        assertDoesNotThrow(() -> userController.deleteUser(testData.get(2).getId()));
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<User> getTestData() {
        // Create user objects
        long time = System.currentTimeMillis();
        User u1 = new User(1, "Marc", "Auberer", "marc.auberer@chillibits.com", "12345678", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING, time, time);
        User u2 = new User(2, "Admin", "User", "info@chillibits.com", "87654321", null, User.ADMINISTRATOR, User.ACTIVE, time, time);
        User u3 = new User(3, "Test", "User", "test@chillibits.com", "", null, User.USER, User.LOCKED, time, time);
        // Add them to test data
        return Arrays.asList(u1, u2, u3);
    }

    private List<UserDto> getAssertData() {
        // Create sensor dto objects
        UserDto ud1 = new UserDto(1, "Marc", "Auberer", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING);
        UserDto ud2 = new UserDto(2, "Admin", "User", null, User.ADMINISTRATOR, User.ACTIVE);
        UserDto ud3 = new UserDto(3, "Test", "User", null, User.USER, User.LOCKED);

        // Add them to test data
        return Arrays.asList(ud1, ud2, ud3);
    }
}