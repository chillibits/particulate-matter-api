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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps();
        // Retrieve stats
        long[] values = new long[5];
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        for(String collectionName : collectionNames) {
            values[0] += getRecordCountFromTimestamp(collectionName, 0, currentTime);
            values[1] += getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime);
            values[2] += getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]);
            values[3] += getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime);
            values[4] += getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]);
        }

        Stats stats = new Stats();
        // Sensor count
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        stats.setSensorsTotal(collectionNames.size());
        stats.setSensorsActive(countActiveSensors(collectionNames));
        // Records count
        stats.setDataRecordsTotal(values[0]);
        stats.setDataRecordsToday(values[1]);
        stats.setDataRecordsYesterday(values[2]);
        stats.setDataRecordsThisMonth(values[3]);
        stats.setDataRecordsPrevMonth(values[4]);
        // Server request count
        stats.setServerRequestsTotal(getServerRequestsCountFromTimestampTotal());
        stats.setServerRequestsTodayApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP, timestamps[1], currentTime));
        stats.setServerRequestsTodayWebApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_WEB, timestamps[1], currentTime));
        stats.setServerRequestsTodayGoogleActions(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_GA, timestamps[1], currentTime));
        stats.setServerRequestsYesterdayApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP, timestamps[2], timestamps[1]));
        stats.setServerRequestsYesterdayWebApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_WEB, timestamps[2], timestamps[1]));
        stats.setServerRequestsYesterdayGoogleActions(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_GA, timestamps[2], timestamps[1]));

        return stats;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stats/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about a specific sensor")
    public Stats getStatsOfSensor(@PathVariable Long chipId) {
        // Initialization
        String collectionName = String.valueOf(chipId);
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps();

        Stats stats = new Stats();
        // Sensor count
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        Set<String> sensorsSet = new HashSet<>();
        sensorsSet.add(collectionName);
        stats.setSensorsActive(countActiveSensors(sensorsSet));
        // Records count
        stats.setDataRecordsTotal(getRecordCountFromTimestamp(collectionName, 0, currentTime));
        stats.setDataRecordsToday(getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime));
        stats.setDataRecordsYesterday(getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]));
        stats.setDataRecordsThisMonth(getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime));
        stats.setDataRecordsPrevMonth(getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]));
        // Server request count
        stats.setServerRequestsTotal(getServerRequestsCountFromTimestampSingleTotal(chipId, 0, currentTime));
        stats.setServerRequestsTodayApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP, chipId, timestamps[1], currentTime));
        stats.setServerRequestsTodayWebApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_WEB, chipId, timestamps[1], currentTime));
        stats.setServerRequestsTodayGoogleActions(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_GA, chipId, timestamps[1], currentTime));
        stats.setServerRequestsYesterdayApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP, chipId, timestamps[2], timestamps[1]));
        stats.setServerRequestsYesterdayWebApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_WEB, chipId, timestamps[2], timestamps[1]));
        stats.setServerRequestsYesterdayGoogleActions(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_GA, chipId, timestamps[2], timestamps[1]));

        return stats;
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private long getRecordCountFromTimestamp(String collectionName, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to)), collectionName);
    }

    private long getServerRequestsCountFromTimestamp(int clientId, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("clientId").is(clientId)), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampTotal() {
        return mongoTemplate.count(new Query(), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampSingle(int clientId, long chipId, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("target").is(chipId).and("clientId").is(clientId)), ConstantUtils.LOG_TABLE_NAME);
    }
    private long getServerRequestsCountFromTimestampSingleTotal(long chipId, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("target").is(chipId).and("clientId")), ConstantUtils.LOG_TABLE_NAME);
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

    private long countActiveSensors(Set<String> collectionNames) {
        long count = 0;
        for(String collectionName : collectionNames) {
            List<DataRecord> latestRecordList =  mongoTemplate.find(Query.query(Criteria.where("timestamp").gte(System.currentTimeMillis() - ConstantUtils.MINUTES_UNTIL_INACTIVITY * 60 * 1000)).limit(1), DataRecord.class, collectionName);
            if(!latestRecordList.isEmpty()) count++;
        }
        return count;
    }
}