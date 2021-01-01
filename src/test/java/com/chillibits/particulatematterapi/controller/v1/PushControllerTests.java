/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.PushDataException;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.dto.DataRecordInsertUpdateDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.service.PushService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Push Controller")
public class PushControllerTests {

    @Autowired
    private PushController pushController;
    @MockBean
    private SensorRepository sensorRepository;

    private final List<DataRecordInsertUpdateDto> testData = getTestData();
    private final List<Sensor> testSensors = getTestSensors();

    @TestConfiguration
    static class PushControllerImplTestContextConfiguration {

        @MockBean
        private MongoTemplate template;

        @Bean
        public PushController pushController() {
            return new PushController();
        }

        @Bean
        public PushService pushService() {
            return new PushService();
        }

        @Bean
        public ModelMapper mapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        when(sensorRepository.findById(testSensors.get(0).getChipId())).thenReturn(Optional.of(testSensors.get(0)));
        when(sensorRepository.save(any(Sensor.class))).thenReturn(null);
    }

    // -------------------------------------------------- Push data ----------------------------------------------------

    @Test
    @DisplayName("Test pushing a data records successfully (X-Header)")
    public void testPushDataXHeader() {
        String result = pushController.pushData(testData.get(0), "esp8266-1234567", "");
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("Test pushing a data records successfully (Header)")
    public void testPushDataHeader() {
        String result = pushController.pushData(testData.get(0), "", "esp8266-1234567");
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("Test pushing a data records successfully (new sensor)")
    public void testPushDataNewSensor() {
        String result = pushController.pushData(testData.get(1), String.valueOf(testData.get(1).getChipId()), "0");
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("Test pushing a data records failure")
    public void testPushDataException() {
        // Try with invalid input
        Exception exception = assertThrows(PushDataException.class, () ->
                pushController.pushData(testData.get(2), String.valueOf(testData.get(2).getChipId()), "0")
        );

        String expectedMessage = new PushDataException(ErrorCode.NO_DATA_VALUES).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<DataRecordInsertUpdateDto> getTestData() {
        // Create SensorDataValues object
        DataRecordInsertUpdateDto.SensorDataValue v1 = new DataRecordInsertUpdateDto.SensorDataValue("SDS_P1", 10.1);
        DataRecordInsertUpdateDto.SensorDataValue v2 = new DataRecordInsertUpdateDto.SensorDataValue("SDS_P2", 5.4);
        DataRecordInsertUpdateDto.SensorDataValue v3 = new DataRecordInsertUpdateDto.SensorDataValue("GPS_lat", 37.4220251);
        DataRecordInsertUpdateDto.SensorDataValue v4 = new DataRecordInsertUpdateDto.SensorDataValue("GPS_lng", -122.0846072);
        DataRecordInsertUpdateDto.SensorDataValue v5 = new DataRecordInsertUpdateDto.SensorDataValue("GPS_height", 3.2);
        // Create data record objects
        long time = System.currentTimeMillis();
        DataRecordInsertUpdateDto d1 = new DataRecordInsertUpdateDto(0, time, "2020-03", new DataRecordInsertUpdateDto.SensorDataValue[]{ v1, v2, v3, v4, v5 }, "No notes");
        DataRecordInsertUpdateDto d2 = new DataRecordInsertUpdateDto(12345678, time, "2020-02", new DataRecordInsertUpdateDto.SensorDataValue[]{ v1, v2 }, "");
        DataRecordInsertUpdateDto d3 = new DataRecordInsertUpdateDto(123456, time, "2018-03", null, "Nothing");
        // Add them to test data
        return Arrays.asList(d1, d2, d3);
    }

    private List<Sensor> getTestSensors() {
        // Create sensor objects
        long time = System.currentTimeMillis();
        Sensor s1 = new Sensor(1234567, null, "2020-01", 0, "No notes", time, time, 0.0, 0.0, 0, "Germany", "Berlin", false, true, true);
        Sensor s2 = new Sensor(12345678, null, "2020-02", 0, "", time, time, 10.0, 30.0, 50, "Germany", "Stuttgart", true, false, true);
        // Add them to test data
        return Arrays.asList(s1, s2);
    }
}