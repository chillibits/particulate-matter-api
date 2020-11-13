/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.StatsDataException;
import com.chillibits.particulatematterapi.model.db.data.StatsItem;
import com.chillibits.particulatematterapi.model.dto.StatsItemDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.service.StatsService;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("logging")
@DisplayName("Stats Controller")
public class StatsControllerTests {

    @Autowired
    private StatsController statsController;
    @Autowired
    private StatsService statsService;
    @MockBean
    private MongoTemplate template;
    @MockBean
    private SensorRepository sensorRepository;

    @TestConfiguration
    static class StatsControllerImplTestContextConfiguration {

        @Bean
        public StatsController statsController() {
            return new StatsController();
        }

        @Bean
        public StatsService statsService() {
            return new StatsService();
        }

        @Bean
        public ModelMapper mapper() {
            return new ModelMapper();
        }
    }

    @Before("")
    public void init() {
        // Setup fake method calls
        when(template.find(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME))
                .thenReturn(Collections.singletonList(getCachedGlobalItem()));
        when(template.getCollectionNames()).thenReturn(new HashSet<>() {{
            add(ConstantUtils.LOG_TABLE_NAME);
            add("12345678");
            add("87654321");
            add(ConstantUtils.STATS_TABLE_NAME);
            add("123345");
        }});
        when(template.count(any(Query.class), eq("87654321"))).thenReturn(1L);
        when(template.count(any(Query.class), eq("123345"))).thenReturn(1L);
        when(template.count(any(Query.class), eq(ConstantUtils.LOG_TABLE_NAME)))
                .thenReturn(999L).thenReturn(16L).thenReturn(0L)
                .thenReturn(1000100000L).thenReturn(1000L).thenReturn(17L).thenReturn(1L).thenReturn(1476L).thenReturn(25L).thenReturn(1L);
        when(sensorRepository.getSensorsMapTotal()).thenReturn(1928);
        when(sensorRepository.getSensorsMapActive(anyLong())).thenReturn(1323);
        when(template.find(Query.query(Criteria.where("chipId").is(12345678)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME))
                .thenReturn(Collections.singletonList(getCachedSingleItem()));
        when(template.count(any(Query.class), eq("12345678")))
                .thenReturn(271235L).thenReturn(1244L).thenReturn(1433L).thenReturn(17556L).thenReturn(21343L);
    }

    // -------------------------------------------- Calculate Timestamps -----------------------------------------------

    @Test
    @DisplayName("Test for calculating timestamp, relative to the current timestamp")
    public void testCalculateTimestamps() {
        long time = 1599407749874L; // Sun Sep 06 2020 15:55:49
        long timezoneOffset = TimeZone.getDefault().getOffset(new Date(time).getTime());
        long[] timestamps = statsService.calculateTimestamps(time);
        assertEquals(time - ConstantUtils.MINUTES_UNTIL_INACTIVITY, timestamps[0]); // Inactivity offset
        assertEquals(1599350400000L /* Sun Sep 06 2020 00:00:00 */ - timezoneOffset, timestamps[1]); // Midnight today
        assertEquals(1599264000000L /* Sat Sep 05 2020 00:00:00 */ - timezoneOffset, timestamps[2]); // Midnight yesterday
        assertEquals(1598918400000L /* Tue Sep 01 2020 00:00:00 */ - timezoneOffset, timestamps[3]); // Midnight 1st of this month
        assertEquals(1596240000000L /* Sat Aug 01 2020 00:00:00 */ - timezoneOffset, timestamps[4]); // Midnight 1st of prev month
    }

    // -------------------------------------------------- Get stats ----------------------------------------------------

    @Test
    @DisplayName("Test for getting the global stats")
    public void testGetStats() {
        StatsItemDto result = statsController.getStats();
        result.setTimestamp(0);
        assertEquals(getAssertItemGlobal(), result);
    }

    // --------------------------------------------- Get stats of sensor -----------------------------------------------

    @Test
    @DisplayName("Test for getting the stats of a single sensor - successful")
    public void testGetStatsOfSensorSuccess() {
        // Execute 3 times to skip 3 x thenReturn for fake function
        for(int i = 0; i < 3; i++)
            template.count(Query.query(Criteria.where("timestamp").gte(0).lte(1)).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);

        StatsItemDto result = statsController.getStatsOfSensor(12345678);
        result.setTimestamp(0);
        assertEquals(getAssertItemSingle(), result);
    }

    @Test
    @DisplayName("Test for getting the stats of a single sensor - failure")
    public void testGetStatsOfSensorFailure() {
        // Try with invalid input
        Exception exception = assertThrows(StatsDataException.class, () ->
                statsController.getStatsOfSensor(1234567)
        );

        String expectedMessage = new StatsDataException(ErrorCode.STATS_ITEM_DOES_NOT_EXIST).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private StatsItem getCachedGlobalItem() {
        return new StatsItem(0, 1599343200000L, 4511L, 1927L, 1322L, 1000000000L, 0L, 0L, 0L, 1475L, 24L, 0L, 536319408L, 7520469L, 37553200L, 0L, 1367779L);
    }

    private StatsItem getCachedSingleItem() {
        return new StatsItem(12345678, 1599256800000L, 0L, 0L, 0L, 1000000L, 12L, 1L, 0L, 20L, 16L, 1L, 271234L, 17555L, 21342L, 1243L, 1432L);
    }

    private StatsItemDto getAssertItemGlobal() {
        return new StatsItemDto(0, 0, 3L, 1928L, 1323L, 1000000000L, 999L, 16L, 0L, 1475L, 24L, 0L, 536319408L, 7520469L, 37553200L, 271237L, 1367779L);
    }

    private StatsItemDto getAssertItemSingle() {
        return new StatsItemDto(12345678, 0, 3L, 1928L, 1323L, 1000100000L, 1000L, 17L, 1L, 1476L, 25L, 1L, 271235L, 17556L, 21343L, 1244L, 1433L);
    }
}