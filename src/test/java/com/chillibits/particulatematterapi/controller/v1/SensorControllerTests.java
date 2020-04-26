/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.SensorDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.SensorCompressedDto;
import com.chillibits.particulatematterapi.model.dto.SensorDto;
import com.chillibits.particulatematterapi.model.io.MapsPlaceResult;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
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
    private final List<SensorDto> assData = getAssertData();
    private final List<SensorCompressedDto> compAssData = getAssertDataCompressed();

    @TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {

        @MockBean
        private LinkRepository linkRepository;

        @Bean
        public SensorController sensorController() {
            return new SensorController();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        Mockito.when(sensorRepository.findAll())
                .thenReturn(testData);
        Mockito.when(sensorRepository.findAllInRadius(0, 0, 100))
                .thenReturn(Arrays.asList(testData.get(1), testData.get(3), testData.get(4)));
        Mockito.when(sensorRepository.findAllPublished())
                .thenReturn(Arrays.asList(testData.get(0), testData.get(2), testData.get(4)));
        Mockito.when(sensorRepository.findAllPublishedInRadius(0, 0, 100))
                .thenReturn(Collections.singletonList(testData.get(4)));
        Mockito.when(sensorRepository.findById(testData.get(0).getChipId()))
                .thenReturn(Optional.ofNullable(testData.get(0)));
        Mockito.when(sensorRepository.save(Mockito.any(Sensor.class)))
                .then(returnsFirstArg());
        Mockito.when(sensorRepository.existsById(anyLong()))
                .thenReturn(false);
        Mockito.when(sensorRepository.existsById(testData.get(2).getChipId()))
                .thenReturn(true);
        Mockito.when(mongoTemplate.getCollectionNames())
                .thenReturn(new HashSet<>(Arrays.asList(
                        String.valueOf(testData.get(0).getChipId()),
                        String.valueOf(testData.get(1).getChipId()),
                        String.valueOf(testData.get(4).getChipId()),
                        String.valueOf(testData.get(5).getChipId())
                )));
        Mockito.when(userRepository.existsById(
                Arrays.asList(testData.get(1).getUserLinks().toArray(new Link[0])).get(0).user.getId()))
                .thenReturn(true);
        Mockito.when(sensorRepository.updateSensor(anyLong(), anyDouble(), anyDouble(), anyString(), anyString(),
                anyLong(), anyString(), anyBoolean(), anyBoolean()))
                .thenReturn(1);
    }

    // --------------------------------------- Get sensor uncompressed -------------------------------------------------

    @Test
    public void testGetAllSensors() throws SensorDataException {
        // Get all sensors
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 0, false);
        assertThat(result).containsExactlyInAnyOrder(assData.get(0), assData.get(1), assData.get(2), assData.get(3), assData.get(4), assData.get(5));
    }

    @Test
    public void testGetOnlyPublishedSensors() throws SensorDataException {
        // Get only published sensors
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 0, true);
        assertThat(result).containsExactlyInAnyOrder(assData.get(0), assData.get(2), assData.get(4));
    }

    @Test
    public void testGetAllSensorsInRadius() throws SensorDataException {
        // Get sensors within radius
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 100, false);
        assertThat(result).containsExactlyInAnyOrder(assData.get(1), assData.get(3), assData.get(4));
    }

    @Test
    public void testGetOnlyPublishedSensorsInRadius() throws SensorDataException {
        // Get only published sensors within radius
        List<SensorDto> result = sensorController.getAllSensors(0, 0, 100, true);
        assertThat(result).containsExactly(assData.get(4));
    }

    @Test
    public void testGetAllSensorsInvalidRadius() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.getAllSensors(0, 0, -100, false)
        );

        String expectedMessage = new SensorDataException(ErrorCodeUtils.INVALID_RADIUS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ---------------------------------------- Get sensor compressed --------------------------------------------------

    @Test
    public void testGetAllSensorsCompressed() throws SensorDataException {
        // Get all sensors
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 0, false);
        assertThat(result).containsExactlyInAnyOrder(compAssData.get(0), compAssData.get(1), compAssData.get(2), compAssData.get(3), compAssData.get(4), compAssData.get(5));
    }

    @Test
    public void testGetOnlyPublishedSensorsCompressed() throws SensorDataException {
        // Get only published sensors
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 0, true);
        assertThat(result).containsExactlyInAnyOrder(compAssData.get(0), compAssData.get(2), compAssData.get(4));
    }

    @Test
    public void testGetAllSensorsInRadiusCompressed() throws SensorDataException {
        // Get sensors within radius
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 100, false);
        assertThat(result).containsExactlyInAnyOrder(compAssData.get(1), compAssData.get(3), compAssData.get(4));
    }

    @Test
    public void testGetOnlyPublishedSensorsInRadiusCompressed() throws SensorDataException {
        // Get only published sensors within radius
        List<SensorCompressedDto> result = sensorController.getAllSensorsCompressed(0, 0, 100, true);
        assertThat(result).containsExactly(compAssData.get(4));
    }

    @Test
    public void testGetAllSensorsInvalidRadiusCompressed() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.getAllSensorsCompressed(0, 0, -100, false)
        );

        String expectedMessage = new SensorDataException(ErrorCodeUtils.INVALID_RADIUS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------- Single sensor ------------------------------------------------------

    @Test
    public void testGetSingleSensor() {
        SensorDto result = sensorController.getSingleSensor(testData.get(0).getChipId());
        assertEquals(assData.get(0), result);
    }

    @Test
    public void testGetSingleSensorNull() {
        SensorDto result = sensorController.getSingleSensor(-1);
        assertThat(result).isNull();
    }

    // --------------------------------------------- Add sensor --------------------------------------------------------

    @Test
    public void testAddSensor() throws SensorDataException {
        Sensor result = sensorController.addSensor(testData.get(1));
        assertEquals(testData.get(1), result);
    }

    @Test
    public void testAddSensorExceptionAlreadyExists() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.addSensor(testData.get(2))
        );

        String expectedMessage = new SensorDataException(ErrorCodeUtils.SENSOR_ALREADY_EXISTS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddSensorExceptionNoDataRecords() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.addSensor(testData.get(3))
        );

        String expectedMessage = new SensorDataException(ErrorCodeUtils.NO_DATA_RECORDS).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddSensorExceptionCannotAssignToUser() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.addSensor(testData.get(4))
        );

        String expectedMessage = new SensorDataException(ErrorCodeUtils.CANNOT_ASSIGN_TO_USER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testAddSensorBlankCountryCity() throws SensorDataException {
        Sensor result = sensorController.addSensor(testData.get(5));
        assertThat(result).hasFieldOrPropertyWithValue("country", MapsPlaceResult.UNKNOWN_COUNTRY);
        assertThat(result).hasFieldOrPropertyWithValue("city", MapsPlaceResult.UNKNOWN_CITY);
    }

    // ------------------------------------------- Update sensor -------------------------------------------------------

    @Test
    public void testUpdateSensor() throws SensorDataException {
        Integer result = sensorController.updateSensor(testData.get(1));
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void testUpdateSensorExceptionCannotAssignToUser() {
        // Try with invalid input
        Exception exception = assertThrows(SensorDataException.class, () ->
                sensorController.updateSensor(testData.get(4))
        );

        String expectedMessage = new SensorDataException(ErrorCodeUtils.CANNOT_ASSIGN_TO_USER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<Sensor> getTestData() {
        long millisecondsTillInactivity = ConstantUtils.MINUTES_UNTIL_INACTIVITY * 60 * 1000;
        // Create user objects
        long time = System.currentTimeMillis();
        User u1 = new User(1, "Marc", "Auberer", "marc.auberer@chillibits.com", "12345678", null, User.OPERATOR, User.EMAIL_CONFIRMATION_PENDING, time, time);
        User u2 = new User(2, "Admin", "User", "info@chillibits.com", "87654321", null, User.ADMINISTRATOR, User.ACTIVE, time, time);
        User u3 = new User(3, "Test", "User", "test@chillibits.com", "12344321", null, User.USER, User.LOCKED, time, time);
        // Create sensor objects
        Sensor s1 = new Sensor(1234567, null, "2020-01", 0, "No notes", time, time, 0.0, 0.0, 0, "Germany", "Berlin", false, true, true);
        Sensor s2 = new Sensor(12345678, null, "2020-02", 0, "", time, time, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false, true);
        Sensor s3 = new Sensor(123456, null, "2020-03", 0, "Test", time, time, 20.0, 90.0, 30, "India", "Agra", true, true, true);
        Sensor s4 = new Sensor(1234568, null, "2020-04", 0, "This is a test", time - 2 * millisecondsTillInactivity, time, 30.0, 70.0, 10, "Russia", "Moskva", false, false, false);
        Sensor s5 = new Sensor(1234563, null, "2020-05", 0, "", time - 2 * millisecondsTillInactivity, time, 40.0, 80.0, 80, "Ireland", "Dublin", true, true, false);
        Sensor s6 = new Sensor(2439573, null, "2020-06", 0, "", time, time, -1, 0, 1, "Bulgaria", "Sofia", false, true, true);
        // Create link objects
        Link l1 = new Link(1, u1, s1, true, "Test sensor", 0, time);
        Link l2 = new Link(2, u2, s2, false, "Test", 50, time);
        Link l3 = new Link(3, u3, s3, false, "This is a test", 100, time);
        Link l4 = new Link(4, u1, s4, true, "Yard", 150, time);
        Link l5 = new Link(5, u3, s5, false, "Garage", 200, time);
        Link l6 = new Link(6, u2, s6, false, "Street", 250, time);
        // Add link to sensor
        s1.setUserLinks(new HashSet<>(Collections.singletonList(l1)));
        s2.setUserLinks(new HashSet<>(Collections.singletonList(l2)));
        s3.setUserLinks(new HashSet<>(Collections.singletonList(l3)));
        s4.setUserLinks(new HashSet<>(Collections.singletonList(l4)));
        s5.setUserLinks(new HashSet<>(Collections.singletonList(l5)));
        s6.setUserLinks(new HashSet<>(Collections.singletonList(l6)));
        // Add them to test data
        return Arrays.asList(s1, s2, s3, s4, s5, s6);
    }

    private List<SensorDto> getAssertData() {
        // Create sensor objects
        SensorDto sd1 = new SensorDto(1234567, "2020-01", 0, "No notes", 0.0, 0.0, 0, "Germany", "Berlin", false, true);
        SensorDto sd2 = new SensorDto(12345678, "2020-02", 0, "", 10.0, 30.0, 50, "Germany", "Stuttgart", true, false);
        SensorDto sd3 = new SensorDto(123456, "2020-03", 0, "Test", 20.0, 90.0, 30, "India", "Agra", true, true);
        SensorDto sd4 = new SensorDto(1234568, "2020-04", 0, "This is a test", 30.0, 70.0, 10, "Russia", "Moskva", false, false);
        SensorDto sd5 = new SensorDto(1234563, "2020-05", 0, "", 40.0, 80.0, 80, "Ireland", "Dublin", true, true);
        SensorDto sd6 = new SensorDto(2439573, "2020-06", 0, "", -1, 0, 1, "Bulgaria", "Sofia", false, true);
        // Add them to test data
        return Arrays.asList(sd1, sd2, sd3, sd4, sd5, sd6);
    }

    private List<SensorCompressedDto> getAssertDataCompressed() {
        // Create sensor objects
        SensorCompressedDto scd1 = new SensorCompressedDto(1234567, 0.0, 0.0, true);
        SensorCompressedDto scd2 = new SensorCompressedDto(12345678, 10.0, 30.0, true);
        SensorCompressedDto scd3 = new SensorCompressedDto(123456, 20.0, 90.0, true);
        SensorCompressedDto scd4 = new SensorCompressedDto(1234568, 30.0, 70.0, false);
        SensorCompressedDto scd5 = new SensorCompressedDto(1234563, 40.0, 80.0, false);
        SensorCompressedDto scd6 = new SensorCompressedDto(2439573, -1, 0, true);
        // Add them to test data
        return Arrays.asList(scd1, scd2, scd3, scd4, scd5, scd6);
    }
}