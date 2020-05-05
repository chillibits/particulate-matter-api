/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Push Controller")
public class PushControllerTests {

    @Autowired
    private PushController pushController;
    @MockBean
    private SensorRepository sensorRepository;
    @MockBean
    private MongoTemplate template;

    private final List<DataRecord> testData = getTestData();
    private final List<Sensor> testSensors = getTestSensors();

    @TestConfiguration
    static class PushControllerImplTestContextConfiguration {

        @Bean
        public PushController pushController() {
            return new PushController();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        Mockito.when(sensorRepository.findById(testSensors.get(0).getChipId())).thenReturn(Optional.of(testSensors.get(0)));
        Mockito.when(sensorRepository.save(any(Sensor.class))).thenReturn(null);
    }

    // -------------------------------------------------- Push data ----------------------------------------------------

    @Test
    @DisplayName("Test pushing a data records successfully (X-Header)")
    public void testPushDataXHeader() {
        String result = pushController.pushData(testData.get(0), "esp8266-" + testData.get(0).getChipId(), "0");
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("Test pushing a data records successfully (Header)")
    public void testPushDataHeader() {
        String result = pushController.pushData(testData.get(0), "0", "esp8266-" + testData.get(0).getChipId());
        assertEquals("ok", result);
    }

    @Test
    @DisplayName("Test pushing a data records successfully (new sensor)")
    public void testPushDataNewSensor() {
        String result = pushController.pushData(testData.get(2), String.valueOf(testData.get(2).getChipId()), "0");
        assertEquals("ok", result);
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<DataRecord> getTestData() {
        // Create SensorDataValues object
        DataRecord.SensorDataValue v1 = new DataRecord.SensorDataValue("SDS_P1", 10.1);
        DataRecord.SensorDataValue v2 = new DataRecord.SensorDataValue("SDS_P2", 5.4);
        DataRecord.SensorDataValue v3 = new DataRecord.SensorDataValue("GPS_lat", 37.4220251);
        DataRecord.SensorDataValue v4 = new DataRecord.SensorDataValue("GPS_lng", -122.0846072);
        DataRecord.SensorDataValue v5 = new DataRecord.SensorDataValue("GPS_height", 3.2);
        // Create data record objects
        long time = System.currentTimeMillis();
        DataRecord d1 = new DataRecord(1234567, time, "2020-03", new DataRecord.SensorDataValue[]{ v1, v2, v3, v4, v5 }, "No notes");
        DataRecord d2 = new DataRecord(12345678, time, "2020-02", null, "");
        DataRecord d3 = new DataRecord(123456, time, "2018-03", null, "Nothing");
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