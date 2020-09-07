/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.DataAccessException;
import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.dto.DataRecordCompressedDto;
import com.chillibits.particulatematterapi.model.dto.DataRecordDto;
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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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

    private final long time = System.currentTimeMillis();
    private final long timestampOffset = 15000; // 2,5 minutes in milliseconds
    private final List<DataRecord> testData = getTestDataForChipId12345678();
    private final List<DataRecordDto> assertData = getAssertDataForChipId12345678();
    private final List<DataRecordCompressedDto> assertDataCompressed = getAssertDataForChipId12345678Compressed();

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
        when(template.find(any(Query.class), eq(DataRecord.class), eq("123456")))
                .thenReturn(getTestDataForChipId123456());
        when(template.find(any(Query.class), eq(DataRecord.class), eq("1234567")))
                .thenReturn(null);
        when(template.find(any(Query.class), eq(DataRecord.class), eq("12345678")))
                .thenReturn(testData);
    }

    // -------------------------------------------- Data for single sensor ---------------------------------------------

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor - successful")
    public void testGetDataRecordsSuccessful() {
        List<DataRecordDto> result = dataController.getDataRecords(123456, time, time + timestampOffset * 3);
        assertThat(result).containsExactlyInAnyOrder(assertData.get(0), assertData.get(1), assertData.get(2), assertData.get(3));
    }

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor - failure")
    public void testGetDataRecordsFailure() {
        // Try with invalid input
        Exception exception = assertThrows(DataAccessException.class, () ->
                dataController.getDataRecords(12345678, time + timestampOffset * 3, time)
        );

        String expectedMessage = new DataAccessException(ErrorCode.INVALID_TIME_RANGE_DATA).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor compressed - successful")
    public void testGetDataRecordsCompressedSuccessful() {
        List<DataRecordCompressedDto> result = dataController.getDataRecordsCompressed(123456, time, time + timestampOffset * 3);
        assertThat(result).containsExactlyInAnyOrder(assertDataCompressed.get(0), assertDataCompressed.get(1),
                assertDataCompressed.get(2), assertDataCompressed.get(3));
    }

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor compressed - failure")
    public void testGetDataRecordsCompressedFailure() {
        // Try with invalid input
        Exception exception = assertThrows(DataAccessException.class, () ->
                dataController.getDataRecordsCompressed(12345678, time + timestampOffset * 3, time)
        );

        String expectedMessage = new DataAccessException(ErrorCode.INVALID_TIME_RANGE_DATA).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for getting the latest data record of a single sensor - successful")
    public void testGetLatestDataRecordSuccessful() {
        DataRecordDto result = dataController.getLatestDataRecord(123456);
        assertEquals(assertData.get(0), result);
    }

    @Test
    @DisplayName("Test for getting the latest data record of a single sensor - failure")
    public void testGetLatestDataRecordFailure() {
        assertNull(dataController.getLatestDataRecord(1234567));
    }

    @Test
    @DisplayName("Test for getting all data records of a single sensor compressed - successful")
    public void testGetAllDataRecordsCompressedSuccessful() {
        List<DataRecordCompressedDto> result = dataController.getAllDataRecordsCompressed(12345678);
        assertThat(result).containsExactlyInAnyOrder(assertDataCompressed.get(0), assertDataCompressed.get(1),
                assertDataCompressed.get(2), assertDataCompressed.get(3), assertDataCompressed.get(4));
    }

    @Test
    @DisplayName("Test for getting all data records of a single sensor compressed - failure")
    public void testGetAllDataRecordsCompressedFailure() {
        assertEquals(0, dataController.getAllDataRecordsCompressed(1234567).size());
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

    private List<DataRecord> getTestDataForChipId12345678() {
        DataRecord.SensorDataValue[] sdv1 = new DataRecord.SensorDataValue[] {
                new DataRecord.SensorDataValue("SDS_P1", 3.3),
                new DataRecord.SensorDataValue("SDS_P2", 1.4)
        };
        DataRecord.SensorDataValue[] sdv2 = new DataRecord.SensorDataValue[] {
                new DataRecord.SensorDataValue("SDS_P1", 3.3),
                new DataRecord.SensorDataValue("SDS_P2", 1.4),
                new DataRecord.SensorDataValue("BME280_temperature", 25.1),
                new DataRecord.SensorDataValue("BME280_humidity", 67.5),
                new DataRecord.SensorDataValue("BME280_pressure", 0)
        };
        DataRecord.SensorDataValue[] sdv3 = new DataRecord.SensorDataValue[] {
                new DataRecord.SensorDataValue("SDS_P1", 3.3),
                new DataRecord.SensorDataValue("SDS_P2", 1.4),
                new DataRecord.SensorDataValue("BME280_temperature", 26.1),
                new DataRecord.SensorDataValue("BME280_humidity", 66.3),
                new DataRecord.SensorDataValue("BME280_pressure", 0)
        };
        DataRecord.SensorDataValue[] sdv4 = new DataRecord.SensorDataValue[] {
                new DataRecord.SensorDataValue("SDS_P1", 3.1),
                new DataRecord.SensorDataValue("SDS_P2", 4.1)
        };
        DataRecord.SensorDataValue[] sdv5 = new DataRecord.SensorDataValue[] {
                new DataRecord.SensorDataValue("SDS_P1", 1.3),
                new DataRecord.SensorDataValue("SDS_P2", 0.6)
        };

        DataRecord r1 = new DataRecord(12345678, time, "2020-07", sdv1, "");
        DataRecord r2 = new DataRecord(12345678, time + timestampOffset, "2020-08", sdv2, "");
        DataRecord r3 = new DataRecord(12345678, time + 2 * timestampOffset, "2020-08", sdv3, "Test");
        DataRecord r4 = new DataRecord(12345678, time + 3 * timestampOffset, "2020-08", sdv4, "");
        DataRecord r5 = new DataRecord(12345678, time + 4 * timestampOffset, "2020-08", sdv5, "Note");
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private List<DataRecord> getTestDataForChipId123456() {
        return getTestDataForChipId12345678().stream()
                .filter( r -> r.getTimestamp() != time + timestampOffset * 4)
                .collect(Collectors.toList());
    }

    private List<DataRecordDto> getAssertDataForChipId12345678() {
        DataRecordDto.SensorDataValue[] sdv1 = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 3.3),
                new DataRecordDto.SensorDataValue("SDS_P2", 1.4)
        };
        DataRecordDto.SensorDataValue[] sdv2 = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 3.3),
                new DataRecordDto.SensorDataValue("SDS_P2", 1.4),
                new DataRecordDto.SensorDataValue("BME280_temperature", 25.1),
                new DataRecordDto.SensorDataValue("BME280_humidity", 67.5),
                new DataRecordDto.SensorDataValue("BME280_pressure", 0)
        };
        DataRecordDto.SensorDataValue[] sdv3 = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 3.3),
                new DataRecordDto.SensorDataValue("SDS_P2", 1.4),
                new DataRecordDto.SensorDataValue("BME280_temperature", 26.1),
                new DataRecordDto.SensorDataValue("BME280_humidity", 66.3),
                new DataRecordDto.SensorDataValue("BME280_pressure", 0)
        };
        DataRecordDto.SensorDataValue[] sdv4 = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 3.1),
                new DataRecordDto.SensorDataValue("SDS_P2", 4.1)
        };
        DataRecordDto.SensorDataValue[] sdv5 = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 1.3),
                new DataRecordDto.SensorDataValue("SDS_P2", 0.6)
        };

        DataRecordDto r1 = new DataRecordDto(time, "2020-07", sdv1, "");
        DataRecordDto r2 = new DataRecordDto(time + timestampOffset, "2020-08", sdv2, "");
        DataRecordDto r3 = new DataRecordDto(time + 2 * timestampOffset, "2020-08", sdv3, "Test");
        DataRecordDto r4 = new DataRecordDto(time + 3 * timestampOffset, "2020-08", sdv4, "");
        DataRecordDto r5 = new DataRecordDto(time + 4 * timestampOffset, "2020-08", sdv5, "Note");
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private List<DataRecordCompressedDto> getAssertDataForChipId12345678Compressed() {
        DataRecordCompressedDto.SensorDataValuesDto[] sdv1 = new DataRecordCompressedDto.SensorDataValuesDto[] {
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P1", 3.3),
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P2", 1.4)
        };
        DataRecordCompressedDto.SensorDataValuesDto[] sdv2 = new DataRecordCompressedDto.SensorDataValuesDto[] {
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P1", 3.3),
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P2", 1.4),
                new DataRecordCompressedDto.SensorDataValuesDto("BME280_temperature", 25.1),
                new DataRecordCompressedDto.SensorDataValuesDto("BME280_humidity", 67.5),
                new DataRecordCompressedDto.SensorDataValuesDto("BME280_pressure", 0)
        };
        DataRecordCompressedDto.SensorDataValuesDto[] sdv3 = new DataRecordCompressedDto.SensorDataValuesDto[] {
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P1", 3.3),
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P2", 1.4),
                new DataRecordCompressedDto.SensorDataValuesDto("BME280_temperature", 26.1),
                new DataRecordCompressedDto.SensorDataValuesDto("BME280_humidity", 66.3),
                new DataRecordCompressedDto.SensorDataValuesDto("BME280_pressure", 0)
        };
        DataRecordCompressedDto.SensorDataValuesDto[] sdv4 = new DataRecordCompressedDto.SensorDataValuesDto[] {
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P1", 3.1),
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P2", 4.1)
        };
        DataRecordCompressedDto.SensorDataValuesDto[] sdv5 = new DataRecordCompressedDto.SensorDataValuesDto[] {
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P1", 1.3),
                new DataRecordCompressedDto.SensorDataValuesDto("SDS_P2", 0.6)
        };

        DataRecordCompressedDto r1 = new DataRecordCompressedDto(time / 1000, sdv1);
        DataRecordCompressedDto r2 = new DataRecordCompressedDto((time + timestampOffset) / 1000, sdv2);
        DataRecordCompressedDto r3 = new DataRecordCompressedDto((time + 2 * timestampOffset) / 1000, sdv3);
        DataRecordCompressedDto r4 = new DataRecordCompressedDto((time + 3 * timestampOffset) / 1000, sdv4);
        DataRecordCompressedDto r5 = new DataRecordCompressedDto((time + 4 * timestampOffset) / 1000, sdv5);
        return Arrays.asList(r1, r2, r3, r4, r5);
    }
}