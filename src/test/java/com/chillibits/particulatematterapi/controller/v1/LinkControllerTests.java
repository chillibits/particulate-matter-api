/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.LinkDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

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

    private final List<Link> testData = getTestData();

    @TestConfiguration
    static class SensorControllerImplTestContextConfiguration {

        @Bean
        public LinkController linkController() {
            return new LinkController();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        Mockito.when(sensorRepository.findById(getTestData().get(0).getSensor().getChipId())).thenReturn(Optional.of(testData.get(0).getSensor()));
        Mockito.when(sensorRepository.findById(getTestData().get(1).getSensor().getChipId())).thenReturn(Optional.of(testData.get(1).getSensor()));
        Mockito.when(sensorRepository.findById(getTestData().get(2).getSensor().getChipId())).thenReturn(Optional.of(testData.get(2).getSensor()));
        Mockito.when(sensorRepository.findById(getTestData().get(3).getSensor().getChipId())).thenReturn(Optional.empty());
        Mockito.when(sensorRepository.getOne(getTestData().get(0).getSensor().getChipId())).thenReturn(testData.get(0).getSensor());
        Mockito.when(sensorRepository.getOne(getTestData().get(1).getSensor().getChipId())).thenReturn(testData.get(1).getSensor());
        Mockito.when(sensorRepository.getOne(getTestData().get(2).getSensor().getChipId())).thenReturn(testData.get(2).getSensor());
        Mockito.when(userRepository.findById(testData.get(0).user.getId())).thenReturn(Optional.of(testData.get(0).user));
        Mockito.when(userRepository.findById(testData.get(1).user.getId())).thenReturn(Optional.of(testData.get(1).user));
        Mockito.when(linkRepository.save(any(Link.class))).then(returnsFirstArg());
        Mockito.when(linkRepository.updateLink(anyInt(), anyBoolean(), anyString(), anyInt())).thenReturn(1);
        Mockito.doNothing().when(linkRepository).deleteById(anyInt());
    }

    // --------------------------------------------------- Add link ----------------------------------------------------

    @Test
    public void testAddLinkSuccessfully() throws LinkDataException {
        Link result = linkController.addLink(testData.get(0), testData.get(0).getSensor().getChipId());
        assertEquals(testData.get(0), result);
    }

    @Test
    public void testAddLinkSensorNotExistingException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.addLink(testData.get(3), testData.get(3).getSensor().getChipId())
        );

        String expectedMessage = new LinkDataException(ErrorCodeUtils.SENSOR_NOT_EXISTING).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddLinkInvalidDataException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.addLink(testData.get(1), testData.get(1).getSensor().getChipId())
        );

        String expectedMessage = new LinkDataException(ErrorCodeUtils.INVALID_LINK_DATA).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddLinkUserNotExistingException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.addLink(testData.get(2), testData.get(2).getSensor().getChipId())
        );

        String expectedMessage = new LinkDataException(ErrorCodeUtils.USER_NOT_EXISTING).getMessage();
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

        String expectedMessage = new LinkDataException(ErrorCodeUtils.INVALID_LINK_DATA).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testUpdateLinkUserNotExistingException() {
        // Try with invalid input
        Exception exception = assertThrows(LinkDataException.class, () ->
                linkController.updateLink(testData.get(2))
        );

        String expectedMessage = new LinkDataException(ErrorCodeUtils.USER_NOT_EXISTING).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    // ------------------------------------------------- Delete link ---------------------------------------------------

    @Test
    public void testDeleteLink() {
        assertDoesNotThrow(() -> linkController.deleteLink(testData.get(2).getId()));
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<Link> getTestData() {
        long time = System.currentTimeMillis();
        // Create user objects
        User u1 = new User(1, "Marc", "Auberer", "marc.auberer@chillibits.com", "12345678", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING, time, time);
        User u2 = new User(2, "Admin", "User", "info@chillibits.com", "87654321", null, User.ADMINISTRATOR, User.ACTIVE, time, time);
        User u3 = new User(3, "Test", "User", "test@chillibits.com", "12344321", null, User.USER, User.LOCKED, time, time);
        // Create sensor objects
        Sensor s1 = new Sensor(1234567, null, "2020-01", 0, "No notes", time, time, 0.0, 0.0, 0, "Germany", "Berlin", false, true, true);
        Sensor s2 = new Sensor(12345678, null, "2020-02", 0, "", time, time, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false, true);
        Sensor s3 = new Sensor(123456, null, "2020-03", 0, "Test", time, time, 20.0, 90.0, 30, "India", "Agra", true, true, true);
        Sensor s4 = new Sensor(12345, null, "2020-04", 0, "Test", time, time, 20.0, 90.0, 30, "India", "Agra", true, true, true);
        // Create link objects
        Link l1 = new Link(0, u1, s1, true, "Test", 0, time);
        Link l2 = new Link(0, u2, s2, true, "", 0, time);
        Link l3 = new Link(0, u3, s3, true, "This is a test", 0, time);
        Link l4 = new Link(0, u3, s4, true, "Test", 0, time);
        // Add them to test data
        return Arrays.asList(l1, l2, l3, l4);
    }
}