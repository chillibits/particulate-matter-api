/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.service.DataService;
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

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Data Controller")
public class DataControllerTests {

    @Autowired
    private DataController dataController;
    @Autowired
    private DataService dataService;
    @MockBean
    private MongoTemplate template;
    @MockBean
    private SensorRepository sensorRepository;

    @TestConfiguration
    static class PushControllerImplTestContextConfiguration {

        @Bean
        public DataController dataController() {
            return new DataController();
        }

        @Bean
        public DataService dataService() {
            return new DataService();
        }

        @Bean
        public ModelMapper mapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls


    }

    // -------------------------------------------- Data for single sensor ---------------------------------------------

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor - successful")
    public void testGetDataRecordsSuccessful() {

    }

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor - failure")
    public void testGetDataRecordsFailure() {

    }

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor compressed - successful")
    public void testGetDataRecordsCompressedSuccessful() {

    }

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor compressed - failure")
    public void testGetDataRecordsCompressedFailure() {

    }

    @Test
    @DisplayName("Test for getting the latest data record of a single sensor - successful")
    public void testGetLatestDataRecordSuccessful() {

    }

    @Test
    @DisplayName("Test for getting the latest data record of a single sensor - failure")
    public void testGetLatestDataRecordFailure() {

    }

    @Test
    @DisplayName("Test for getting the latest data record of a single sensor compressed - successful")
    public void testGetLatestDataRecordCompressedSuccessful() {

    }

    @Test
    @DisplayName("Test for getting the latest data record of a single sensor compressed - failure")
    public void testGetLatestDataRecordCompressedFailure() {

    }

    // ------------------------------------------ Data for multiple sensors --------------------------------------------

    @Test
    @DisplayName("Test for getting the average record of several sensors - successful")
    public void testGetDataAverageMultipleSensorsSuccessful() {

    }

    @Test
    @DisplayName("Test for getting the average record of several sensors - failure")
    public void testGetDataAverageMultipleSensorsFailure() {

    }

    // ----------------------------------------------- Data for country ------------------------------------------------

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific country in a certain timespan - successful")
    public void testGetDataCountrySuccessful() {

    }

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific country in a certain timespan - failure")
    public void testGetDataCountryFailure() {

    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific country in a certain timespan - successful")
    public void testGetDataCountryLatestSuccessful() {

    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific country in a certain timespan - failure")
    public void testGetDataCountryLatestFailure() {

    }

    // ------------------------------------------------- Data for city -------------------------------------------------

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific city in a certain timespan - successful")
    public void testGetDataCitySuccessful() {

    }

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific city in a certain timespan - failure")
    public void testGetDataCityFailure() {

    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific city in a certain timespan - successful")
    public void testGetDataCityLatestSuccessful() {

    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific city in a certain timespan - failure")
    public void testGetDataCityLatestFailure() {

    }

    // --------------------------------------------- Chart data functions ----------------------------------------------

    @Test
    @DisplayName("Test for getting json data for a chart for a single sensor for a certain timespan - successful")
    public void testGetChartDataSuccessful() {

    }

    @Test
    @DisplayName("Test for getting json data for a chart for a single sensor for a certain timespan - failure")
    public void testGetChartDataFailure() {

    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a country for a certain timespan - successful")
    public void testGetChartDataCountrySuccessful() {

    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a country for a certain timespan - failure")
    public void testGetChartDataCountryFailure() {

    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a city for a certain timespan - successful")
    public void testGetChartDataCitySuccessful() {

    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a city for a certain timespan - failure")
    public void testGetChartDataCityFailure() {

    }

    // -------------------------------------------------- Test data ----------------------------------------------------


}