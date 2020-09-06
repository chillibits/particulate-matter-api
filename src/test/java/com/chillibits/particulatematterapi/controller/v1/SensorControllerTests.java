/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.SensorDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.LinkInsertUpdateDto;
import com.chillibits.particulatematterapi.model.dto.SensorCompressedDto;
import com.chillibits.particulatematterapi.model.dto.SensorDto;
import com.chillibits.particulatematterapi.model.dto.SensorInsertUpdateDto;
import com.chillibits.particulatematterapi.model.dto.UserDto;
import com.chillibits.particulatematterapi.model.io.MapsPlaceResult;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.service.SensorService;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Sensor Controller")
public class SensorControllerTests {

    @Autowired
    private SensorController sensorController;
    @MockBean
    private SensorRepository sensorRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private MongoTemplate mongoTemplate;

    private final List<Sensor> testData = getTestData();
    private final List<SensorInsertUpdateDto> testInsertUpdateData = getTestInsertUpdateData();
    private final List<SensorDto> assertData = getAssertData();
    private final List<SensorCompressedDto> compAssData = getAssertDataCompressed();

    @TestConfiguration
    static class SensorControllerImplTestContextConfiguration {

        @MockBean
        private LinkRepository linkRepository;

        @Bean
        public SensorController sensorController() {
            return new SensorController();
        }

        @Bean
        public SensorService sensorService() {
            return new SensorService();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        when(sensorRepository.findAll()).thenReturn(testData);
        when(sensorRepository.findAllInRadius(0, 0, 100)).thenReturn(Arrays.asList(testData.get(1), testData.get(3), testData.get(4)));
        when(sensorRepository.findAllPublished()).thenReturn(Arrays.asList(testData.get(0), testData.get(2), testData.get(4)));
        when(sensorRepository.findAllPublishedInRadius(0, 0, 100)).thenReturn(Collections.singletonList(testData.get(4)));
        when(sensorRepository.findById(testInsertUpdateData.get(0).getChipId())).thenReturn(Optional.ofNullable(testData.get(0)));
        when(sensorRepository.save(any(Sensor.class))).then(returnsFirstArg());
        when(sensorRepository.existsById(anyLong())).thenReturn(false);
        when(sensorRepository.existsById(testInsertUpdateData.get(2).getChipId())).thenReturn(true);
        when(sensorRepository.existsById(testInsertUpdateData.get(6).getChipId())).thenReturn(true);
        when(sensorRepository.existsById(testInsertUpdateData.get(7).getChipId())).thenReturn(true);
        when(sensorRepository.updateSensor(any(Sensor.class))).thenReturn(1);
        doNothing().when(sensorRepository).deleteById(anyLong());
        // MongoTemplate
        when(mongoTemplate.getCollectionNames())
                .thenReturn(new HashSet<>(Arrays.asList(
                        String.valueOf(testInsertUpdateData.get(0).getChipId()),
                        String.valueOf(testInsertUpdateData.get(1).getChipId()),
                        String.valueOf(testInsertUpdateData.get(4).getChipId()),
                        String.valueOf(testInsertUpdateData.get(5).getChipId())
                )));
        // UserRepository
        when(userRepository.existsById(anyInt())).thenReturn(true);
        int userId = new ArrayList<>(testInsertUpdateData.get(4).getUserLinks()).get(0).getUser().getId();
        when(userRepository.existsById(userId)).thenReturn(false);
        when(userRepository.findById(new ArrayList<>(testInsertUpdateData.get(6).getUserLinks()).get(0).getUser().getId()))
                .thenReturn(Optional.of(new ArrayList<>(testData.get(6).getUserLinks()).get(0).getUser()));
    }

    // --------------------------------------- Get sensor uncompressed -------------------------------------------------

    @Test
    @DisplayName("Test for getting all sensors successfully")
    public void testGetAllSensors() throws SensorDataException {
        // Get all sensors
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 0, false);
        assertThat(result).containsExactlyInAnyOrder(assertData.toArray(SensorDto[]::new));
    }

    @Test
    @DisplayName("Test for getting all published sensors successfully")
    public void testGetOnlyPublishedSensors() throws SensorDataException {
        // Get only published sensors
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 0, true);
        assertThat(result).containsExactlyInAnyOrder(assertData.get(0), assertData.get(2), assertData.get(4));
    }

    @Test
    @DisplayName("Test for getting all sensors in a specific radius successfully")
    public void testGetAllSensorsInRadius() throws SensorDataException {
        // Get sensors within radius
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 100, false);
        assertThat(result).containsExactlyInAnyOrder(assertData.get(1), assertData.get(3), assertData.get(4));
    }

    @Test
    @DisplayName("Test for getting all published sensors in a specific radius successfully")
    public void testGetOnlyPublishedSensorsInRadius() throws SensorDataException {
        // Get only published sensors within radius
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 100, true);
        assertThat(result).containsExactly(assertData.get(4));
    }

    @Test
    @DisplayName("Test for getting all sensors with invalid radius, triggering a InvalidRadius exception")
    public void testGetAllSensorsInvalidRadius() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.getAllSensors(10, 10, -100, false)
        );

        String expectedMessage = new SensorDataException(ErrorCode.INVALID_RADIUS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ---------------------------------------- Get sensor compressed --------------------------------------------------

    @Test
    @DisplayName("Test for getting all sensors successfully in a compressed form")
    public void testGetAllSensorsCompressed() throws SensorDataException {
        // Get all sensors
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 0, false);
        assertThat(result).containsExactlyInAnyOrder(compAssData.get(0), compAssData.get(1), compAssData.get(2),
                compAssData.get(3), compAssData.get(4), compAssData.get(5), compAssData.get(6), compAssData.get(7));
    }

    @Test
    @DisplayName("Test for getting all published sensors successfully in a compressed form")
    public void testGetOnlyPublishedSensorsCompressed() throws SensorDataException {
        // Get only published sensors
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 0, true);
        assertThat(result).containsExactlyInAnyOrder(compAssData.get(0), compAssData.get(2), compAssData.get(4));
    }

    @Test
    @DisplayName("Test for getting all sensors in a specific radius successfully in a compressed form")
    public void testGetAllSensorsInRadiusCompressed() throws SensorDataException {
        // Get sensors within radius
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 100, false);
        assertThat(result).containsExactlyInAnyOrder(compAssData.get(1), compAssData.get(3), compAssData.get(4));
    }

    @Test
    @DisplayName("Test for getting all published sensors in a specific radius successfully in a compressed form")
    public void testGetOnlyPublishedSensorsInRadiusCompressed() throws SensorDataException {
        // Get only published sensors within radius
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 100, true);
        assertThat(result).containsExactly(compAssData.get(4));
    }

    @Test
    @DisplayName("Test for getting all compressed sensors with invalid radius, triggering a InvalidRadius exception")
    public void testGetAllSensorsInvalidRadiusCompressed() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.getAllSensorsCompressed(10, 10, -100, false)
        );

        String expectedMessage = new SensorDataException(ErrorCode.INVALID_RADIUS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------- Single sensor ------------------------------------------------------

    @Test
    @DisplayName("Test for getting a single sensor successfully")
    public void testGetSingleSensor() {
        SensorDto result = sensorController.getSingleSensor(testInsertUpdateData.get(0).getChipId());
        assertEquals(assertData.get(0), result);
    }

    @Test
    @DisplayName("Test for getting a single sensor with an invalid chip id")
    public void testGetSingleSensorNull() {
        SensorDto result = sensorController.getSingleSensor(-1);
        assertThat(result).isNull();
    }

    // --------------------------------------------- Add sensor --------------------------------------------------------

    @Test
    @DisplayName("Test for adding a sensor successfully")
    public void testAddSensor() throws SensorDataException {
        SensorDto result = sensorController.addSensor(testInsertUpdateData.get(1));
        SensorDto expected = assertData.get(1);
        result.setFirmwareVersion(expected.getFirmwareVersion());
        result.setCreationTimestamp(expected.getCreationTimestamp());
        result.setCity(expected.getCity());
        result.setCountry(expected.getCountry());
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test for adding a sensor, triggering a SensorAlreadyExists exception")
    public void testAddSensorExceptionSensorAlreadyExists() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.addSensor(testInsertUpdateData.get(2))
        );

        String expectedMessage = new SensorDataException(ErrorCode.SENSOR_ALREADY_EXISTS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for adding a sensor, triggering a NoDataRecords exception")
    public void testAddSensorExceptionNoDataRecords() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.addSensor(testInsertUpdateData.get(3))
        );

        String expectedMessage = new SensorDataException(ErrorCode.NO_DATA_RECORDS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for adding a sensor, triggering a CannotAssignToUser exception")
    public void testAddSensorExceptionCannotAssignToUser() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.addSensor(testInsertUpdateData.get(4))
        );

        String expectedMessage = new SensorDataException(ErrorCode.CANNOT_ASSIGN_TO_USER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for adding a sensor with blank country and city")
    public void testAddSensorBlankCountryCity() throws SensorDataException {
        SensorDto result = sensorController.addSensor(testInsertUpdateData.get(5));
        assertThat(result).hasFieldOrPropertyWithValue("country", MapsPlaceResult.UNKNOWN_COUNTRY);
        assertThat(result).hasFieldOrPropertyWithValue("city", MapsPlaceResult.UNKNOWN_CITY);
    }

    // ------------------------------------------- Update sensor -------------------------------------------------------

    @Test
    @DisplayName("Test for updating a sensor successfully")
    public void testUpdateSensor() throws SensorDataException {
        int result = sensorController.updateSensor(testInsertUpdateData.get(6));
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Test for updating a sensor, triggering a SensorNotExisting exception")
    public void testUpdateSensorExceptionSensorNotExisting() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.updateSensor(testInsertUpdateData.get(0))
        );

        String expectedMessage = new SensorDataException(ErrorCode.SENSOR_NOT_EXISTING).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for updating a sensor, triggering a CannotAssignToUser exception")
    public void testUpdateSensorExceptionCannotAssignToUser() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.updateSensor(testInsertUpdateData.get(7))
        );

        String expectedMessage = new SensorDataException(ErrorCode.CANNOT_ASSIGN_TO_USER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ------------------------------------------- Delete sensor -------------------------------------------------------

    @Test
    @DisplayName("Test for deleting a sensor successfully")
    public void testDeleteSensor() throws SensorDataException {
        assertDoesNotThrow(() -> sensorController.deleteSensor(testInsertUpdateData.get(7).getChipId()));
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<Sensor> getTestData() {
        long millisecondsTillInactivity = ConstantUtils.MINUTES_UNTIL_INACTIVITY * 60 * 1000;
        // Create user objects
        long time = System.currentTimeMillis();
        User u1 = new User(1, "Marc", "Auberer", "marc.auberer@chillibits.com", "7TTU1ew7OpNa5XKvv0hc", "12345678", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING, time, time);
        User u2 = new User(2, "Admin", "User", "info@chillibits.com", "iNjwuU2GzCpDqjWLwYc5", "87654321", null, User.ADMINISTRATOR, User.ACTIVE, time, time);
        User u3 = new User(3, "Test", "User", "test@chillibits.com", "AHsY6peje1PyTTonZrZm", "12344321", null, User.USER, User.LOCKED, time, time);
        // Create sensor objects
        Sensor s1 = new Sensor(1234567, null, "2020-01", 0, "No notes", time, time, 0.0, 0.0, 0, "Germany", "Berlin", false, true, true);
        Sensor s2 = new Sensor(12345678, null, "2020-02", 0, "", time, time, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false, true);
        Sensor s3 = new Sensor(123456, null, "2020-03", 0, "Test", time, time, 20.0, 90.0, 30, "India", "Agra", true, true, true);
        Sensor s4 = new Sensor(1234568, null, "2020-04", 0, "This is a test", time - 2 * millisecondsTillInactivity, time, 30.0, 70.0, 10, "Russia", "Moskva", false, false, false);
        Sensor s5 = new Sensor(1234563, null, "2020-05", 0, "", time - 2 * millisecondsTillInactivity, time, 40.0, 80.0, 80, "Ireland", "Dublin", true, true, false);
        Sensor s6 = new Sensor(2439573, null, "2020-06", 0, "", time, time, -1, 0, 1, "Bulgaria", "Sofia", false, true, true);
        Sensor s7 = new Sensor(24395731, null, "2020-07", 0, "", time, time, -1, 0, 1, "Belgium", "Brussels", true, true, true);
        Sensor s8 = new Sensor(24395732, null, "2020-08", 0, "", time, time, -1, 0, 1, "Netherlands", "Amsterdam", false, true, true);
        // Create link objects
        Link l1 = new Link(1, u1, s1, true, "Test sensor", 0, time);
        Link l2 = new Link(2, u2, s2, false, "Test", 50, time);
        Link l3 = new Link(3, u3, s3, false, "This is a test", 100, time);
        Link l4 = new Link(4, u1, s4, true, "Yard", 150, time);
        Link l5 = new Link(5, u3, s5, false, "Garage", 200, time);
        Link l6 = new Link(6, u2, s6, false, "Street", 250, time);
        Link l7 = new Link(6, u2, s6, false, "", 200, time);
        Link l8 = new Link(6, u3, s6, false, "", 140, time);
        // Add link to sensor
        s1.setUserLinks(new HashSet<>(Collections.singletonList(l1)));
        s2.setUserLinks(new HashSet<>(Collections.singletonList(l2)));
        s3.setUserLinks(new HashSet<>(Collections.singletonList(l3)));
        s4.setUserLinks(new HashSet<>(Collections.singletonList(l4)));
        s5.setUserLinks(new HashSet<>(Collections.singletonList(l5)));
        s6.setUserLinks(new HashSet<>(Collections.singletonList(l6)));
        s7.setUserLinks(new HashSet<>(Collections.singletonList(l7)));
        s8.setUserLinks(new HashSet<>(Collections.singletonList(l8)));
        // Add them to test data
        return Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8);
    }

    private List<SensorInsertUpdateDto> getTestInsertUpdateData() {
        // Create user objects
        UserDto u1 = new UserDto(1, "Marc", "Auberer", "marc.auberer@chillibits.com", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING);
        UserDto u2 = new UserDto(2, "Admin", "User", "info@chillibits.com", null, User.ADMINISTRATOR, User.ACTIVE);
        UserDto u3 = new UserDto(3, "Test", "User", "test@chillibits.com", null, User.USER, User.LOCKED);
        // Create sensorDto objects
        SensorDto s1 = new SensorDto(1234567, "2020-01", 0, 0.0, 0.0, 0, "Germany", "Berlin", false, true);
        SensorDto s2 = new SensorDto(12345678, "2020-02", 0, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false);
        SensorDto s3 = new SensorDto(123456, "2020-03", 0, 20.0, 90.0, 30, "India", "Agra", true, true);
        SensorDto s4 = new SensorDto(1234568, "2020-04", 0, 30.0, 70.0, 10, "Russia", "Moskva", false, false);
        SensorDto s5 = new SensorDto(1234563, "2020-05", 0, 40.0, 80.0, 80, "Ireland", "Dublin", true, true);
        SensorDto s6 = new SensorDto(2439573, "2020-06", 0, -1, 0, 1, "Bulgaria", "Sofia", false, true);
        SensorDto s7 = new SensorDto(24395731, "2020-07", 0, -1, 0, 1, "Belgium", "Brussels", true, true);
        SensorDto s8 = new SensorDto(24395732, "2020-08", 0, -1, 0, 1, "Netherlands", "Amsterdam", false, true);
        // Create sensorInsertUpdateDto objects
        SensorInsertUpdateDto siu1 = new SensorInsertUpdateDto(1234567, null, 0.0, 0.0, 0, false, true);
        SensorInsertUpdateDto siu2 = new SensorInsertUpdateDto(12345678, null, 10.0, 30.0, 50, true, false);
        SensorInsertUpdateDto siu3 = new SensorInsertUpdateDto(123456, null, 20.0, 90.0, 30, true, true);
        SensorInsertUpdateDto siu4 = new SensorInsertUpdateDto(1234568, null, 30.0, 70.0, 10, false, false);
        SensorInsertUpdateDto siu5 = new SensorInsertUpdateDto(1234563, null, 40.0, 80.0, 80, true, true);
        SensorInsertUpdateDto siu6 = new SensorInsertUpdateDto(2439573, null, -1, 0, 1, false, true);
        SensorInsertUpdateDto siu7 = new SensorInsertUpdateDto(24395731, null, -1, 0, 1, true, true);
        SensorInsertUpdateDto siu8 = new SensorInsertUpdateDto(24395732, null, -1, 0, 1, false, true);
        // Create link objects
        LinkInsertUpdateDto l1 = new LinkInsertUpdateDto(1, u1, s1, true, "Test sensor", 0);
        LinkInsertUpdateDto l2 = new LinkInsertUpdateDto(2, u2, s2, false, "Test", 50);
        LinkInsertUpdateDto l3 = new LinkInsertUpdateDto(3, u3, s3, false, "This is a test", 100);
        LinkInsertUpdateDto l4 = new LinkInsertUpdateDto(4, u1, s4, true, "Yard", 150);
        LinkInsertUpdateDto l5 = new LinkInsertUpdateDto(5, u3, s5, false, "Garage", 200);
        LinkInsertUpdateDto l6 = new LinkInsertUpdateDto(6, u2, s6, false, "Street", 250);
        LinkInsertUpdateDto l7 = new LinkInsertUpdateDto(6, u2, s6, false, "", 200);
        LinkInsertUpdateDto l8 = new LinkInsertUpdateDto(6, u3, s6, false, "", 140);
        // Add link to sensor
        siu1.setUserLinks(new HashSet<>(Collections.singletonList(l1)));
        siu2.setUserLinks(new HashSet<>(Collections.singletonList(l2)));
        siu3.setUserLinks(new HashSet<>(Collections.singletonList(l3)));
        siu4.setUserLinks(new HashSet<>(Collections.singletonList(l4)));
        siu5.setUserLinks(new HashSet<>(Collections.singletonList(l5)));
        siu6.setUserLinks(new HashSet<>(Collections.singletonList(l6)));
        siu7.setUserLinks(new HashSet<>(Collections.singletonList(l7)));
        siu8.setUserLinks(new HashSet<>(Collections.singletonList(l8)));
        // Add them to test data
        return Arrays.asList(siu1, siu2, siu3, siu4, siu5, siu6, siu7, siu8);
    }

    private List<SensorDto> getAssertData() {
        // Create sensor dto objects
        SensorDto sd1 = new SensorDto(1234567, "2020-01", 0, 0.0, 0.0, 0, "Germany", "Berlin", false, true);
        SensorDto sd2 = new SensorDto(12345678, "2020-02", 0, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false);
        SensorDto sd3 = new SensorDto(123456, "2020-03", 0, 20.0, 90.0, 30, "India", "Agra", true, true);
        SensorDto sd4 = new SensorDto(1234568, "2020-04", 0, 30.0, 70.0, 10, "Russia", "Moskva", false, false);
        SensorDto sd5 = new SensorDto(1234563, "2020-05", 0, 40.0, 80.0, 80, "Ireland", "Dublin", true, true);
        SensorDto sd6 = new SensorDto(2439573, "2020-06", 0, -1, 0, 1, "Bulgaria", "Sofia", false, true);
        SensorDto sd7 = new SensorDto(24395731, "2020-07", 0, -1, 0, 1, "Belgium", "Brussels", true, true);
        SensorDto sd8 = new SensorDto(24395732, "2020-08", 0, -1, 0, 1, "Netherlands", "Amsterdam", false, true);

        // Add them to test data
        return Arrays.asList(sd1, sd2, sd3, sd4, sd5, sd6, sd7, sd8);
    }

    private List<SensorCompressedDto> getAssertDataCompressed() {
        // Create sensor objects
        SensorCompressedDto scd1 = new SensorCompressedDto(1234567, 0.0, 0.0, true);
        SensorCompressedDto scd2 = new SensorCompressedDto(12345678, 10.0, 30.0, true);
        SensorCompressedDto scd3 = new SensorCompressedDto(123456, 20.0, 90.0, true);
        SensorCompressedDto scd4 = new SensorCompressedDto(1234568, 30.0, 70.0, false);
        SensorCompressedDto scd5 = new SensorCompressedDto(1234563, 40.0, 80.0, false);
        SensorCompressedDto scd6 = new SensorCompressedDto(2439573, -1, 0, true);
        SensorCompressedDto scd7 = new SensorCompressedDto(24395731, -1, 0, true);
        SensorCompressedDto scd8 = new SensorCompressedDto(24395732, -1, 0, true);
        // Add them to test data
        return Arrays.asList(scd1, scd2, scd3, scd4, scd5, scd6, scd7, scd8);
    }
}