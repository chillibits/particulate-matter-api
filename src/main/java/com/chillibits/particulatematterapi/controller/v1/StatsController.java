/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

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
import org.springframework.web.bind.annotation.PathVariable;
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
        long[] timestamps = calculateTimestamps();
        // Retrieve stats
        long[] values = new long[5];
        for(String collectionName : mongoTemplate.getCollectionNames()) {
            values[0] += getRecordCountFromTimestamp(collectionName, 0, System.currentTimeMillis());
            values[1] += getRecordCountFromTimestamp(collectionName, timestamps[1], System.currentTimeMillis());
            values[2] += getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]);
            values[3] += getRecordCountFromTimestamp(collectionName, timestamps[3], System.currentTimeMillis());
            values[4] += getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]);
        }

        Stats stats = new Stats();
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        stats.setDataRecordsTotal(values[0]);
        stats.setDataRecordsToday(values[1]);
        stats.setDataRecordsYesterday(values[2]);
        stats.setDataRecordsThisMonth(values[3]);
        stats.setDataRecordsPrevMonth(values[4]);

        // TODO: Implement remaining stats determination

        return stats;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stats/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about a specific sensor")
    public Stats getStatsOfSensor(@PathVariable Long chipId) {
        // Initialization
        String collectionName = String.valueOf(chipId);
        long[] timestamps = calculateTimestamps();
        // Retrieve stats
        Stats stats = new Stats();
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        stats.setDataRecordsTotal(getRecordCountFromTimestamp(collectionName, 0, System.currentTimeMillis()));
        stats.setDataRecordsToday(getRecordCountFromTimestamp(collectionName, timestamps[1], System.currentTimeMillis()));
        stats.setDataRecordsYesterday(getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]));
        stats.setDataRecordsThisMonth(getRecordCountFromTimestamp(collectionName, timestamps[3], System.currentTimeMillis()));
        stats.setDataRecordsPrevMonth(getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]));

        // TODO: Implement remaining stats determination

        return stats;
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private long getRecordCountFromTimestamp(String collectionName, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to)), collectionName);
    }

    private long[] calculateTimestamps() {
        long[] timestamps = new long[5];
        // Calculate timestamps
        timestamps[0] = System.currentTimeMillis() - ConstantUtils.MINUTES_UNTIL_INACTIVITY;
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        timestamps[1] = cal.getTimeInMillis(); // Midnight today
        timestamps[2] = timestamps[1] - 24 * 60 * 60 * 1000; // Midnight yesterday
        cal.set(Calendar.DAY_OF_MONTH , 0);
        timestamps[3] = cal.getTimeInMillis(); // Midnight 1st of this month
        cal.add(Calendar.MONTH, -1);
        timestamps[4] = cal.getTimeInMillis(); // Midnight 1st of previous month
        return timestamps;
    }
}