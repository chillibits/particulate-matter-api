/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.LogAccessException;
import com.chillibits.particulatematterapi.model.db.data.LogItem;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

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

    @TestConfiguration
    static class SensorControllerImplTestContextConfiguration {

        @Bean
        public LogController logController() {
            return new LogController();
        }
    }

    @Before
    public void init() {
        Mockito.when(template.find(Query.query(Criteria.where("timestamp").gte(anyLong()).lte(anyLong())), LogItem.class))
                .thenReturn(testData);
        Mockito.when(template.find(Query.query(Criteria.where("timestamp").gte(anyLong()).lte(anyLong()).and("userId").is(2)), LogItem.class))
                .thenReturn(testData.stream().filter(item -> item.getUserId() == 2).collect(Collectors.toList()));
        Mockito.when(template.find(Query.query(Criteria.where("timestamp").gte(anyLong()).lte(anyLong()).and("userId").is(2)), LogItem.class))
                .thenReturn(testData.stream().filter(item -> item.getUserId() == 2).collect(Collectors.toList()));
        Mockito.when(template.find(Query.query(Criteria.where("timestamp").gte(anyLong()).lte(anyLong()).and("clientId").is(ConstantUtils.CLIENT_ID_PMAPP)), LogItem.class))
                .thenReturn(testData.stream().filter(item -> item.getUserId() == ConstantUtils.CLIENT_ID_PMAPP).collect(Collectors.toList()));
        Mockito.when(template.find(Query.query(Criteria.where("timestamp").gte(anyLong()).lte(anyLong()).and("action").regex("Get data")), LogItem.class))
                .thenReturn(testData.stream().filter(item -> item.getAction().equals("Get data")).collect(Collectors.toList()));
    }

    // --------------------------------------------------- Get logs ----------------------------------------------------

    /*@Test
    public void testGetAllLogsSuccessfully() throws LogAccessException {
        List<LogItem> result = logController.getAllLogs(time - 20000, time + 20000);
        assertThat(result).containsExactlyInAnyOrder(testData.toArray(LogItem[]::new));
    }*/

    @Test
    public void testGetAllLogsInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getAllLogs(-1, -5)
        );

        String expectedMessage = new LogAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_LOG).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
    }

    /*@Test
    public void testGetAllLogsByTargetSuccessfully() throws LogAccessException {
        List<LogItem> result = logController.getLogsByTarget("User 2", time - 20000, time + 20000);
        assertThat(result).containsExactlyInAnyOrder(testData.toArray(LogItem[]::new));
    }*/

    @Test
    public void testGetAllLogsByTargetInvalidTimeRangeException() {
        // Try with invalid input
        Exception exception = assertThrows(LogAccessException.class, () ->
                logController.getLogsByTarget("User 2", time + 20000, time - 20000)
        );

        String expectedMessage = new LogAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_LOG).getMessage();
        Assert.assertEquals(expectedMessage, exception.getMessage());
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
}