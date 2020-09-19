/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.db.data.StatsItem;
import com.chillibits.particulatematterapi.repository.SensorRepository;
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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Stats Service")
public class StatsServiceTests {

    @Autowired
    private StatsService statsService;
    @MockBean
    private MongoTemplate template;

    @TestConfiguration
    static class StatsServiceImplTestContextConfiguration {

        @MockBean
        private SensorRepository sensorRepository;

        @Bean
        public StatsService statsService() {
            return new StatsService();
        }

        @Bean
        public ModelMapper mapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        when(template.getCollectionNames()).thenReturn(new HashSet<>() {{
            add(ConstantUtils.LOG_TABLE_NAME);
            add("12345678");
            add("87654321");
            add(ConstantUtils.STATS_TABLE_NAME);
            add("123345");
        }});
        when(template.find(any(Query.class), eq(StatsItem.class), eq(ConstantUtils.STATS_TABLE_NAME)))
                .thenReturn(new ArrayList<>())
                .thenReturn(Collections.singletonList(getCachedGlobalItem()));
    }

    @Test
    public void testCalculateStats() {
        // Call with no cached item
        statsService.calculateStats();
        // Call with cached item
        statsService.calculateStats();
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private StatsItem getCachedGlobalItem() {
        return new StatsItem(0, 1599343200000L, 4511L, 1927L, 1322L, 1000000000L, 0L, 0L, 0L, 1475L, 24L, 0L, 536319408L, 7520469L, 37553200L, 0L, 1367779L);
    }
}