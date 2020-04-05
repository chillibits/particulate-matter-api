/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.io.Stats;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.GregorianCalendar;

@RestController
@Api(value = "Stats REST Endpoint", tags = "stats")
public class StatsController {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, path = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about the API")
    public Stats getStats() {
        // Initialization
        long minMeasurementTimestamp = System.currentTimeMillis() - ConstantUtils.MINUTES_UNTIL_INACTIVITY;

        // Calculate timestamps
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long timestampStartToday = cal.getTimeInMillis();
        long timestampStartYesterday = timestampStartToday - 24 * 60 * 60 * 1000;
        cal.set(Calendar.DAY_OF_MONTH , 0);
        long timestampStartMonth = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long timestampStartPreviousMonth = cal.getTimeInMillis();

        // Retrieve stats
        Stats stats = new Stats();
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(minMeasurementTimestamp));
        stats.setDataRecordsTotal(getRecordCountFromTimestamp(0, System.currentTimeMillis()));
        stats.setDataRecordsToday(getRecordCountFromTimestamp(timestampStartToday, System.currentTimeMillis()));
        stats.setDataRecordsYesterday(getRecordCountFromTimestamp(timestampStartYesterday, timestampStartToday));
        stats.setDataRecordsThisMonth(getRecordCountFromTimestamp(timestampStartMonth, System.currentTimeMillis()));
        stats.setDataRecordsPrevMonth(getRecordCountFromTimestamp(timestampStartPreviousMonth, timestampStartMonth));

        // TODO: Implement stats determination

        return stats;
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private long getRecordCountFromTimestamp(long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to)), DataRecord.class);
    }
}