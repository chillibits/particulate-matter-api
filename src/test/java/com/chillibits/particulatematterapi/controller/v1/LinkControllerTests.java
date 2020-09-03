/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.LinkDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.LinkDto;
import com.chillibits.particulatematterapi.model.dto.LinkInsertUpdateDto;
import com.chillibits.particulatematterapi.model.dto.SensorDto;
import com.chillibits.particulatematterapi.model.dto.UserDto;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.service.LinkService;
import org.junit.Assert;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Link Controller")
public class LinkControllerTests {

    @Autowired
    private LinkController linkController;
    @MockBean
    private LinkRepository linkRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SensorRepository sensorRepository;

    private final long time = System.currentTimeMillis();
    private final List<LinkInsertUpdateDto> testData = getInsertUpdateTestData();
    private final List<User> userTestData = getUserTestData();
    private final List<Sensor> sensorTestData = getSensorTestData();
    private final List<LinkDto> assertData = getAssertData();

    @TestConfiguration
    static class SensorControllerImplTestContextConfiguration {

        @Bean
        public LinkController linkController() {
            return new LinkController();
        }

        @Bean
        public LinkService linkService() {
            return new LinkService();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        Mockito.when(sensorRepository.findById(getInsertUpdateTestData().get(0).getSensor().getChipId())).thenReturn(Optional.of(sensorTestData.get(0)));
        Mockito.when(sensorRepository.findById(getInsertUpdateTestData().get(1).getSensor().getChipId())).thenReturn(Optional.of(sensorTestData.get(1)));
        Mockito.when(sensorRepository.findById(getInsertUpdateTestData().get(2).getSensor().getChipId())).thenReturn(Optional.of(sensorTestData.get(2)));
        Mockito.when(sensorRepository.findById(getInsertUpdateTestData().get(3).getSensor().getChipId())).thenReturn(Optional.empty());
        Mockito.when(sensorRepository.getOne(getInsertUpdateTestData().get(0).getSensor().getChipId())).thenReturn(sensorTestData.get(0));
        Mockito.when(sensorRepository.getOne(getInsertUpdateTestData().get(1).getSensor().getChipId())).thenReturn(sensorTestData.get(1));
        Mockito.when(sensorRepository.getOne(getInsertUpdateTestData().get(2).getSensor().getChipId())).thenReturn(sensorTestData.get(2));
        Mockito.when(userRepository.findById(testData.get(0).getUser().getId())).thenReturn(Optional.of(userTestData.get(0)));
        Mockito.when(userRepository.findById(testData.get(1).getUser().getId())).thenReturn(Optional.of(userTestData.get(1)));
        Mockito.when(linkRepository.save(any(Link.class))).then(returnsFirstArg());
        Mockito.when(linkRepository.updateLink(any(Link.class))).thenReturn(1);
        Mockito.doNothing().when(linkRepository).deleteById(anyInt());
    }

    // --------------------------------------------------- Add link ----------------------------------------------------

    @Test
    public void testAddLinkSuccessfully() throws LinkDataException {
        LinkDto result = linkController.addLink(testData.get(0));
        assertEquals(assertData.get(0), result);
    }

    @Test
    public void testAddLinkSensorNotExistingException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.addLink(testData.get(3))
        );

        String expectedMessage = new LinkDataException(ErrorCode.SENSOR_NOT_EXISTING).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddLinkInvalidDataException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.addLink(testData.get(1))
        );

        String expectedMessage = new LinkDataException(ErrorCode.INVALID_LINK_DATA).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddLinkUserNotExistingException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.addLink(testData.get(2))
        );

        String expectedMessage = new LinkDataException(ErrorCode.USER_NOT_EXISTING).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    // ------------------------------------------------- Update link ---------------------------------------------------

    @Test
    public void testUpdateLinkSuccessfully() throws LinkDataException {
        int result = linkController.updateLink(testData.get(0));
        assertEquals(1, result);
    }

    @Test
    public void testUpdateLinkInvalidDataException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.updateLink(testData.get(1))
        );

        String expectedMessage = new LinkDataException(ErrorCode.INVALID_LINK_DATA).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testUpdateLinkUserNotExistingException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.updateLink(testData.get(2))
        );

        String expectedMessage = new LinkDataException(ErrorCode.USER_NOT_EXISTING).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    // ------------------------------------------------- Delete link ---------------------------------------------------

    @Test
    public void testDeleteLink() {
        assertDoesNotThrow(() -> linkController.deleteLink(testData.get(2).getId()));
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<LinkInsertUpdateDto> getInsertUpdateTestData() {
        // Create user objects
        UserDto u1 = new UserDto(1, "Marc", "Auberer", "marc.auberer@chillibits.com", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING);
        UserDto u2 = new UserDto(2, "Admin", "User", "info@chillibits.com", null, User.ADMINISTRATOR, User.ACTIVE);
        UserDto u3 = new UserDto(3, "Test", "User", "test@chillibits.com", null, User.USER, User.LOCKED);
        // Create sensor objects
        SensorDto s1 = new SensorDto(1234567, "2020-01", 0, 0.0, 0.0, 0, "Germany", "Berlin", false, true);
        SensorDto s2 = new SensorDto(12345678, "2020-02", 0, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false);
        SensorDto s3 = new SensorDto(123456, "2020-03", 0, 20.0, 90.0, 30, "India", "Agra", true, true);
        SensorDto s4 = new SensorDto(12345, "2020-04", 0, 20.0, 90.0, 30, "India", "Agra", true, true);
        // Create link objects
        LinkInsertUpdateDto l1 = new LinkInsertUpdateDto(0, u1, s1, true, "Test", 0);
        LinkInsertUpdateDto l2 = new LinkInsertUpdateDto(0, u2, s2, true, "", 0);
        LinkInsertUpdateDto l3 = new LinkInsertUpdateDto(0, u3, s3, true, "This is a test", 0);
        LinkInsertUpdateDto l4 = new LinkInsertUpdateDto(0, u3, s4, true, "Test", 0);
        // Add them to test data
        return Arrays.asList(l1, l2, l3, l4);
    }

    private List<User> getUserTestData() {
        User u1 = new User(1, "Marc", "Auberer", "marc.auberer@chillibits.com", "7TTU1ew7OpNa5XKvv0hc", "12345678", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING, time, time);
        User u2 = new User(2, "Admin", "User", "info@chillibits.com", "iNjwuU2GzCpDqjWLwYc5", "87654321", null, User.ADMINISTRATOR, User.ACTIVE, time, time);
        User u3 = new User(3, "Test", "User", "test@chillibits.com", "AHsY6peje1PyTTonZrZm", "12344321", null, User.USER, User.LOCKED, time, time);
        return Arrays.asList(u1, u2, u3);
    }

    private List<Sensor> getSensorTestData() {
        Sensor s1 = new Sensor(1234567, null, "2020-01", 0, "No notes", time, time, 0.0, 0.0, 0, "Germany", "Berlin", false, true, true);
        Sensor s2 = new Sensor(12345678, null, "2020-02", 0, "", time, time, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false, true);
        Sensor s3 = new Sensor(123456, null, "2020-03", 0, "Test", time, time, 20.0, 90.0, 30, "India", "Agra", true, true, true);
        Sensor s4 = new Sensor(12345, null, "2020-04", 0, "Test", time, time, 20.0, 90.0, 30, "India", "Agra", true, true, true);
        return Arrays.asList(s1, s2, s3, s4);
    }

    private List<LinkDto> getAssertData() {
        // Create sensor objects
        SensorDto s1 = new SensorDto(1234567, "2020-01", 0, 0.0, 0.0, 0, "Germany", "Berlin", false, true);
        SensorDto s2 = new SensorDto(12345678, "2020-02", 0, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false);
        SensorDto s3 = new SensorDto(123456, "2020-03", 0, 20.0, 90.0, 30, "India", "Agra", true, true);
        SensorDto s4 = new SensorDto(12345, "2020-04", 0, 20.0, 90.0, 30, "India", "Agra", true, true);
        // Create link objects
        LinkDto l1 = new LinkDto(0, s1, true, "Test", 0);
        LinkDto l2 = new LinkDto(0,  s2, true, "", 0);
        LinkDto l3 = new LinkDto(0, s3, true, "This is a test", 0);
        LinkDto l4 = new LinkDto(0, s4, true, "Test", 0);
        // Add them to test data
        return Arrays.asList(l1, l2, l3, l4);
    }
}