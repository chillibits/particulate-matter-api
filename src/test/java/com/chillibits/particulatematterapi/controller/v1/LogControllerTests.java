/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.LogAccessException;
import com.chillibits.particulatematterapi.model.db.data.LogItem;
import com.chillibits.particulatematterapi.model.dto.LogItemDto;
import com.chillibits.particulatematterapi.service.LogService;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Link Controller")
public class LogControllerTests {

    @Autowired
    private LogController logController;
    @MockBean
    private MongoTemplate template;

    private final long time = System.currentTimeMillis();
    private final List<LogItem> testData = getTestData();
    private final List<LogItemDto> assertData = getAssertData();

    @TestConfiguration
    static class LogControllerImplTestContextConfiguration {

        @Bean
        public LogController logController() {
            return new LogController();
        }

        @Bean
        public LogService logService() {
            return new LogService();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        when(template.find(any(Query.class), eq(LogItem.class)))
                .thenReturn(testData)
                .thenReturn(testData.stream().filter(item -> item.getTarget().equals("User 2")).collect(Collectors.toList()))
                .thenReturn(testData.stream().filter(item -> item.getClientId() == ConstantUtils.CLIENT_ID_PMAPP).collect(Collectors.toList()))
                .thenReturn(testData.stream().filter(item -> item.getUserId() == 2).collect(Collectors.toList()))
                .thenReturn(testData.stream().filter(item -> item.getAction().equals("Get data")).collect(Collectors.toList()));
    }

    // --------------------------------------------------- Get logs ----------------------------------------------------

    @Test
    public void testGetAllLogsSuccessfully() throws LogAccessException {
        List<LogItemDto> result = logController.getAllLogs(time - 20000, time + 20000);
        assertThat(result).containsExactlyInAnyOrder(assertData.toArray(LogItemDto[]::new));
    }

    @Test
    public void testGetAllLogsInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getAllLogs(-1, -5)
        );

        String expectedMessage = new LogAccessException(ErrorCode.INVALID_TIME_RANGE_LOG).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetAllLogsByTargetSuccessfully() throws LogAccessException {
        // Execute 1 time to skip 1 x thenReturn for fake function
        template.find(Query.query(Criteria.where("timestamp").gte(0)), LogItem.class);

        List<LogItemDto> result = logController.getLogsByTarget("User 2", time - 20000, time + 20000);
        List<LogItemDto> expected = assertData.stream().filter(item -> item.getTarget().equals("User 2")).collect(Collectors.toList());
        assertThat(result).containsExactlyInAnyOrder(expected.toArray(LogItemDto[]::new));
    }

    @Test
    public void testGetAllLogsByTargetInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getLogsByTarget("User 2", time + 20000, time - 20000)
        );

        String expectedMessage = new LogAccessException(ErrorCode.INVALID_TIME_RANGE_LOG).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetAllLogsByClientSuccessfully() throws LogAccessException {
        // Execute 2 times to skip 2 x thenReturn for fake function
        for(int i = 0; i < 2; i++) template.find(Query.query(Criteria.where("timestamp").gte(0)), LogItem.class);

        List<LogItemDto> result = logController.getLogsByClient(ConstantUtils.CLIENT_ID_PMAPP, time - 20000, time + 20000);
        List<LogItemDto> expected = assertData.stream().filter(item -> item.getClientId() == ConstantUtils.CLIENT_ID_PMAPP).collect(Collectors.toList());
        assertThat(result).containsExactlyInAnyOrder(expected.toArray(LogItemDto[]::new));
    }

    @Test
    public void testGetAllLogsByClientInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getLogsByClient(ConstantUtils.CLIENT_ID_PMAPP, time + 20000, time - 20000)
        );

        String expectedMessage = new LogAccessException(ErrorCode.INVALID_TIME_RANGE_LOG).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetAllLogsByUserSuccessfully() throws LogAccessException {
        // Execute 3 times to skip 3 x thenReturn for fake function
        for(int i = 0; i < 3; i++) template.find(Query.query(Criteria.where("timestamp").gte(0)), LogItem.class);

        List<LogItemDto> result = logController.getLogsByUser(2, time - 20000, time + 20000);
        List<LogItemDto> expected = assertData.stream().filter(item -> item.getUserId() == 2).collect(Collectors.toList());
        assertThat(result).containsExactlyInAnyOrder(expected.toArray(LogItemDto[]::new));
    }

    @Test
    public void testGetAllLogsByUserInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getLogsByUser(2, time + 20000, time - 20000)
        );

        String expectedMessage = new LogAccessException(ErrorCode.INVALID_TIME_RANGE_LOG).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetAllLogsByActionSuccessfully() throws LogAccessException {
        // Execute 4 times to skip 4 x thenReturn for fake function
        for(int i = 0; i < 4; i++) template.find(Query.query(Criteria.where("timestamp").gte(0)), LogItem.class);

        List<LogItemDto> result = logController.getLogsByAction("Get data", time - 20000, time + 20000);
        List<LogItemDto> expected = assertData.stream().filter(item -> item.getAction().equals("Get data")).collect(Collectors.toList());
        assertThat(result).containsExactlyInAnyOrder(expected.toArray(LogItemDto[]::new));
    }

    @Test
    public void testGetAllLogsByActionInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getLogsByAction("Get data", time + 20000, time - 20000)
        );

        String expectedMessage = new LogAccessException(ErrorCode.INVALID_TIME_RANGE_LOG).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<LogItem> getTestData() {
        LogItem i1 = new LogItem(time, ConstantUtils.UNKNOWN_CLIENT_ID, 1, "Get data", "Sensor 112345");
        LogItem i2 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP, 2, "Get data", "Sensor 123245");
        LogItem i3 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP, 3, "Change user data", "User 3");
        LogItem i4 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP, 1, "Get data", "Sensor 12345");
        LogItem i5 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP_GA, 2, "Get sensors", "User 2");
        LogItem i6 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP, 3, "Add sensor", "Sensor 5498");
        LogItem i7 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP_GA, 1, "Get data", "Sensor 1233445");
        LogItem i8 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP_WEB, 2, "Get stats", "User 2");
        LogItem i9 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP_WEB, 3, "Get data", "Sensor 1234345");
        LogItem i10 = new LogItem(time, ConstantUtils.CLIENT_ID_PMAPP, 4, "Get data", "Sensor 123445");

        return Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8, i9, i10);
    }

    private List<LogItemDto> getAssertData() {
        LogItemDto i1 = new LogItemDto(time, ConstantUtils.UNKNOWN_CLIENT_ID, 1, "Get data", "Sensor 112345");
        LogItemDto i2 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP, 2, "Get data", "Sensor 123245");
        LogItemDto i3 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP, 3, "Change user data", "User 3");
        LogItemDto i4 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP, 1, "Get data", "Sensor 12345");
        LogItemDto i5 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP_GA, 2, "Get sensors", "User 2");
        LogItemDto i6 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP, 3, "Add sensor", "Sensor 5498");
        LogItemDto i7 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP_GA, 1, "Get data", "Sensor 1233445");
        LogItemDto i8 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP_WEB, 2, "Get stats", "User 2");
        LogItemDto i9 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP_WEB, 3, "Get data", "Sensor 1234345");
        LogItemDto i10 = new LogItemDto(time, ConstantUtils.CLIENT_ID_PMAPP, 4, "Get data", "Sensor 123445");

        return Arrays.asList(i1, i2, i3, i4, i5, i6, i7, i8, i9, i10);
    }
}