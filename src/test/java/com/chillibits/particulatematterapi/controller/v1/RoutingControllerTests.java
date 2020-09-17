/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Routing Controller")
public class RoutingControllerTests {

    @Autowired
    private RoutingController routingController;
    @MockBean
    private UserRepository userRepository;

    private final List<User> testData = getTestData();

    @TestConfiguration
    static class RoutingControllerImplTestContextConfiguration {

        @Bean
        public RoutingController routingController() {
            return new RoutingController();
        }

        @Bean
        public UserService userService() {
            return new UserService();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }

        @MockBean
        private JavaMailSender mailSender;
    }

    @Before
    public void init() {
        // Setup fake method calls
        when(userRepository.findByConfirmationToken(testData.get(0).getConfirmationToken())).thenReturn(testData.get(0));
        when(userRepository.findByConfirmationToken(testData.get(1).getConfirmationToken())).thenReturn(testData.get(1));
    }

    // --------------------------------------------- Test Swagger Redirect ---------------------------------------------

    @Test
    public void testSwaggerRedirect() {
        String expected = "redirect:/swagger-ui/index.html";
        assertEquals(expected, routingController.swagger());
    }

    // ----------------------------------------- Test User Account Confirmation ----------------------------------------

    @Test
    public void testUserConfirmationSuccessful() {
        String expected = "redirect:https://www.chillibits.com/pmapp?p=confirmation/success";
        assertEquals(expected, routingController.confirmAccount("HottTfNWoyIyShN76gKl"));
    }

    @Test
    public void testUserConfirmationFailure() {
        String expected = "redirect:https://www.chillibits.com/pmapp?p=confirmation/failure";
        assertEquals(expected, routingController.confirmAccount("xhx4MXH8iLKVvR7Q8l8c"));
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<User> getTestData() {
        long time = System.currentTimeMillis();
        User u1 = new User(1, "Marc", "Auberer", "marc.auberer@chillibits.com", "xhx4MXH8iLKVvR7Q8l8c", "12345678", null, User.OPERATOR, User.ACTIVE, time, time);
        User u2 = new User(2, "John", "Doe", "info@chillibits.com", "HottTfNWoyIyShN76gKl", "password", null, User.USER, User.EMAIL_CONFIRMATION_PENDING, time, time);
        return Arrays.asList(u1, u2);
    }
}