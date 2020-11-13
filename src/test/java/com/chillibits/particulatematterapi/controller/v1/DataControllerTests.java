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
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("logging")
@DisplayName("Data Controller")
public class DataControllerTests {

    @Autowired
    private DataController dataController;
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
    static class DataControllerImplTestContextConfiguration {

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

    @Before("")
    public void init() {
        // Setup fake method calls
        when(template.find(any(Query.class), eq(DataRecord.class), eq("12345")))
                .thenReturn(getTestDataForChipId12345());
        when(template.find(any(Query.class), eq(DataRecord.class), eq("123456")))
                .thenReturn(getTestDataForChipId123456());
        when(template.find(any(Query.class), eq(DataRecord.class), eq("1234567")))
                .thenReturn(null);
        when(template.find(any(Query.class), eq(DataRecord.class), eq("12345678")))
                .thenReturn(testData);
        when(sensorRepository.getChipIdsOfSensorFromCountry("Germany")).thenReturn(Arrays.asList(12345L, 12345678L, 123456L));
        when(sensorRepository.getChipIdsOfSensorFromCity("Germany", "Berlin")).thenReturn(Arrays.asList(12345L, 123456L));
    }

    // -------------------------------------------- Data for single sensor ---------------------------------------------

    @Test
    @DisplayName("Test for getting data from timespan of a single sensor - successful")
    public void testGetDataRecordsSuccessful() {
        List<DataRecordDto> result = dataController.getDataRecords(123456, time - timestampOffset * 3, time);
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
        List<DataRecordCompressedDto> result = dataController.getDataRecordsCompressed(123456, time - timestampOffset * 3, time);
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
        DataRecordDto result = dataController.getDataAverageMultipleSensors(new Long[]{ 12345L, 123456L, 12345678L });
        result.setTimestamp(time);
        assertEquals(getAssertDataAverage(), result);
    }

    @Test
    @DisplayName("Test for getting the average record of several sensors - failure")
    public void testGetDataAverageMultipleSensorsFailure() {
        DataRecordDto result = dataController.getDataAverageMultipleSensors(new Long[]{ 1234L, 123456789L, 1234567890L });
        assertEquals(new DataRecordDto(), result);
    }

    // ----------------------------------------------- Data for country ------------------------------------------------

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific country in a certain timespan - successful")
    public void testGetDataCountrySuccessful() {
        List<DataRecordCompressedDto> result = dataController.getDataCountry("Germany", time - 4 * timestampOffset, time);

        List<DataRecordCompressedDto> assertDate12345 = getAssertDataCompressedChipId12345();
        assertThat(result).containsExactlyInAnyOrder(assertDataCompressed.get(0), assertDataCompressed.get(1), assertDataCompressed.get(2),
                assertDataCompressed.get(3), assertDataCompressed.get(4), assertDataCompressed.get(0), assertDataCompressed.get(1),
                assertDataCompressed.get(2), assertDataCompressed.get(3), assertDate12345.get(0), assertDate12345.get(1),
                assertDate12345.get(2), assertDate12345.get(3), assertDate12345.get(4));
    }

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific country in a certain timespan - failure")
    public void testGetDataCountryFailure() {
        List<DataRecordCompressedDto> result = dataController.getDataCountry("NonExisting", time - 4 * timestampOffset, time);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific country in a certain timespan - successful")
    public void testGetDataCountryLatestSuccessful() {
        DataRecordDto result = dataController.getDataCountryLatest("Germany");
        result.setTimestamp(time);
        assertEquals(getAssertDataAverage(), result);
    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific country in a certain timespan - failure")
    public void testGetDataCountryLatestFailure() {
        DataRecordDto result = dataController.getDataCountryLatest("NonExisting");
        assertEquals(new DataRecordDto(), result);
    }

    // ------------------------------------------------- Data for city -------------------------------------------------

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific city in a certain timespan - successful")
    public void testGetDataCitySuccessful() {
        List<DataRecordCompressedDto> result = dataController.getDataCity("Germany", "Berlin", time - 4 * timestampOffset, time);

        List<DataRecordCompressedDto> assertDate12345 = getAssertDataCompressedChipId12345();
        assertThat(result).containsExactlyInAnyOrder(assertDataCompressed.get(0), assertDataCompressed.get(1),
                assertDataCompressed.get(2), assertDataCompressed.get(3), assertDate12345.get(0), assertDate12345.get(1),
                assertDate12345.get(2), assertDate12345.get(3), assertDate12345.get(4));
    }

    @Test
    @DisplayName("Test for getting all data records of sensors from a specific city in a certain timespan - failure")
    public void testGetDataCityFailure() {
        List<DataRecordCompressedDto> result = dataController.getDataCity("Germany", "NonExisting", time - 4 * timestampOffset, time);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific city in a certain timespan - successful")
    public void testGetDataCityLatestSuccessful() {
        DataRecordDto result = dataController.getDataCityLatest("Germany", "Berlin");
        result.setTimestamp(time);
        assertEquals(getAssertDataAverageCity(), result);
    }

    @Test
    @DisplayName("Test for getting the average record of sensors from a specific city in a certain timespan - failure")
    public void testGetDataCityLatestFailure() {
        DataRecordDto result = dataController.getDataCityLatest("Germany", "NonExisting");
        assertEquals(new DataRecordDto(), result);
    }

    // --------------------------------------------- Chart data functions ----------------------------------------------

    @Test
    @DisplayName("Test for getting json data for a chart for a single sensor for a certain timespan - successful")
    public void testGetChartDataSuccessful() {
        String result = dataController.getChartData(12345678L, time - 4 * timestampOffset, time, 0, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result.indexOf("responseTime") + 14;
        String value = result.substring(indexStart, result.indexOf(",", indexStart));
        result = result.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataAssertString(), result);
    }

    @Test
    @DisplayName("Test for getting json data for a chart for a single sensor for a certain timespan - failure")
    public void testGetChartDataFailure() {
        String result = dataController.getChartData(123456789L, time - 4 * timestampOffset, time, 0, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result.indexOf("responseTime") + 14;
        String value = result.substring(indexStart, result.indexOf("}", indexStart));
        result = result.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataAssertStringNoData(), result);
    }

    @Test
    @DisplayName("Test for getting json data for a chart for a single sensor for a certain timespan - failure")
    public void testGetChartDataInvalidInputData() {
        // Try it with a fieldIndex out of range
        String result1 = dataController.getChartData(123456789L, time - 4 * timestampOffset, time, 10, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result1.indexOf("responseTime") + 14;
        String value = result1.substring(indexStart, result1.indexOf("}", indexStart));
        result1 = result1.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataAssertStringNoData(), result1);

        // Try with a negative fieldIndex
        Exception exception = assertThrows(DataAccessException.class, () ->
                dataController.getChartData(123456789L, time - 4 * timestampOffset, time, -2, 1)
        );

        String expectedMessage = new DataAccessException(ErrorCode.INVALID_FIELD_INDEX).getMessage();
        assertEquals(expectedMessage, exception.getMessage());

        // Try with a negative mergeCount
        exception = assertThrows(DataAccessException.class, () ->
                dataController.getChartData(123456789L, time - 4 * timestampOffset, time, 0, -1)
        );

        expectedMessage = new DataAccessException(ErrorCode.INVALID_MERGE_COUNT).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a country for a certain timespan - successful")
    public void testGetChartDataCountrySuccessful() {
        String result = dataController.getChartDataCountry("Germany", time - 4 * timestampOffset, time, 0, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result.indexOf("responseTime") + 14;
        String value = result.substring(indexStart, result.indexOf(",", indexStart));
        result = result.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataCountryAssertString(), result);
    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a country for a certain timespan - failure")
    public void testGetChartDataCountryFailure() {
        String result = dataController.getChartDataCountry("Germany", time - 4 * timestampOffset, time, 0, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result.indexOf("responseTime") + 14;
        String value = result.substring(indexStart, result.indexOf("}", indexStart));
        result = result.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataCountryAssertStringNoData(), result);
    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a city for a certain timespan - successful")
    public void testGetChartDataCitySuccessful() {
        String result = dataController.getChartDataCity("Germany", "Berlin", time - 4 * timestampOffset, time, 0, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result.indexOf("responseTime") + 14;
        String value = result.substring(indexStart, result.indexOf(",", indexStart));
        result = result.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataCityAssertString(), result);
    }

    @Test
    @DisplayName("Test for getting json data for a chart for sensors from a city for a certain timespan - failure")
    public void testGetChartDataCityFailure() {
        String result = dataController.getChartDataCity("Germany", "Berlin", time - 4 * timestampOffset, time, 0, 1);

        // Replace responseTime, cause it's not the same every time
        int indexStart = result.indexOf("responseTime") + 14;
        String value = result.substring(indexStart, result.indexOf("}", indexStart));
        result = result.replace("responseTime\":" + value, "responseTime\":0");

        assertEquals(getChartDataCityAssertStringNoData(), result);
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

        DataRecord r1 = new DataRecord(12345678, time - 4 * timestampOffset, "2020-07", sdv1, "");
        DataRecord r2 = new DataRecord(12345678, time - 3 *timestampOffset, "2020-08", sdv2, "");
        DataRecord r3 = new DataRecord(12345678, time - 2 * timestampOffset, "2020-08", sdv3, "Test");
        DataRecord r4 = new DataRecord(12345678, time - timestampOffset, "2020-08", sdv4, "");
        DataRecord r5 = new DataRecord(12345678, time, "2020-08", sdv5, "Note");
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private List<DataRecord> getTestDataForChipId123456() {
        return getTestDataForChipId12345678().stream()
                .filter(r -> r.getTimestamp() != time)
                .collect(Collectors.toList());
    }

    private List<DataRecord> getTestDataForChipId12345() {
        return getTestDataForChipId12345678().stream()
                .map(r -> {
                    DataRecord.SensorDataValue[] newSensorDataValues = r.getSensorDataValues().clone();
                    for (DataRecord.SensorDataValue newSensorDataValue : newSensorDataValues)
                        newSensorDataValue.setValue(newSensorDataValue.getValue() / 2);
                    return new DataRecord(r.getChipId(), r.getTimestamp(), r.getFirmwareVersion(), newSensorDataValues, r.getNote());
                }).collect(Collectors.toList());
    }

    private List<DataRecordCompressedDto> getAssertDataCompressedChipId12345() {
        return getAssertDataForChipId12345678Compressed().stream()
                .map(r -> {
                    DataRecordCompressedDto.SensorDataValuesDto[] newSensorDataValues = r.getSensorDataValues().clone();
                    for (DataRecordCompressedDto.SensorDataValuesDto newSensorDataValue : newSensorDataValues)
                        newSensorDataValue.setValue(newSensorDataValue.getValue() / 2);
                    return new DataRecordCompressedDto(r.getTimestamp(), newSensorDataValues);
                }).collect(Collectors.toList());
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

        DataRecordDto r1 = new DataRecordDto(time - 4 * timestampOffset, "2020-07", sdv1, "");
        DataRecordDto r2 = new DataRecordDto(time - 3 * timestampOffset, "2020-08", sdv2, "");
        DataRecordDto r3 = new DataRecordDto(time - 2 * timestampOffset, "2020-08", sdv3, "Test");
        DataRecordDto r4 = new DataRecordDto(time - timestampOffset, "2020-08", sdv4, "");
        DataRecordDto r5 = new DataRecordDto(time, "2020-08", sdv5, "Note");
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

        DataRecordCompressedDto r1 = new DataRecordCompressedDto((time - 4 * timestampOffset) / 1000, sdv1);
        DataRecordCompressedDto r2 = new DataRecordCompressedDto((time - 3 * timestampOffset) / 1000, sdv2);
        DataRecordCompressedDto r3 = new DataRecordCompressedDto((time - 2 * timestampOffset) / 1000, sdv3);
        DataRecordCompressedDto r4 = new DataRecordCompressedDto((time - timestampOffset) / 1000, sdv4);
        DataRecordCompressedDto r5 = new DataRecordCompressedDto(time / 1000, sdv5);
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private DataRecordDto getAssertDataAverage() {
        DataRecordDto.SensorDataValue[] sensorDataValues = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 2.75),
                new DataRecordDto.SensorDataValue("SDS_P2", 1.167)
        };
        return new DataRecordDto(time, null, sensorDataValues, "");
    }

    private DataRecordDto getAssertDataAverageCity() {
        DataRecordDto.SensorDataValue[] sensorDataValues = new DataRecordDto.SensorDataValue[] {
                new DataRecordDto.SensorDataValue("SDS_P1", 2.475),
                new DataRecordDto.SensorDataValue("SDS_P2", 1.05)
        };
        return new DataRecordDto(time, null, sensorDataValues, "");
    }

    private String getChartDataAssertString() {
        return "{\"sensorCount\":1,\"field\":\"SDS_P1\",\"responseTime\":0,\"values\":[[" + (time - 4 * timestampOffset)
                + ",3.3],[" + (time - 3 * timestampOffset) + ",3.3],[" + (time - 2 * timestampOffset) + ",3.3],[" +
                (time - timestampOffset) + ",3.1],[" + time + ",1.3]]}";
    }

    private String getChartDataAssertStringNoData() {
        return "{\"sensorCount\":1,\"responseTime\":0}";
    }

    private String getChartDataCountryAssertString() {
        return "{\"sensorCount\":3,\"field\":\"SDS_P1\",\"responseTime\":0,\"values\":[[" + (time - 4 * timestampOffset) + ",2.461]]}";
    }

    private String getChartDataCountryAssertStringNoData() {
        return "{\"sensorCount\":3,\"field\":\"SDS_P1\",\"responseTime\":0}";
    }

    private String getChartDataCityAssertString() {
        return "{\"sensorCount\":2,\"field\":\"SDS_P1\",\"responseTime\":0,\"values\":[[" + (time - 4 * timestampOffset) + ",2.239]]}";
    }

    private String getChartDataCityAssertStringNoData() {
        return "{\"sensorCount\":2,\"field\":\"SDS_P1\",\"responseTime\":0}";
    }
}