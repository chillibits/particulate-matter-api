/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.db.data.StatsItem;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@Api(value = "Stats REST Endpoint", tags = "stats")
public class StatsController {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, path = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about the API")
    public StatsItem getStats() {
        // Initialization
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        StatsItem newItem = new StatsItem();
        // Load already calculated item from cache table
        List<StatsItem> items = mongoTemplate.find(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        if(!items.isEmpty()) newItem = items.get(0);
        // Retrieve newItem
        Set<String> collectionNames = getDataCollections();
        // Data records
        long dataRecordsToday = 0;
        for(String collectionName : collectionNames)
            dataRecordsToday += getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime);
        newItem.setDataRecordsToday(dataRecordsToday);
        // Sensor count
        newItem.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        newItem.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        newItem.setSensorsTotal(collectionNames.size());
        // Server request count
        newItem.setServerRequestsTodayApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP, timestamps[1], currentTime));
        newItem.setServerRequestsTodayWebApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_WEB, timestamps[1], currentTime));
        newItem.setServerRequestsTodayGoogleActions(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_GA, timestamps[1], currentTime));

        // Save calculated values to caching table
        mongoTemplate.remove(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        mongoTemplate.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        return newItem;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stats/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about a specific sensor")
    public StatsItem getStatsOfSensor(@PathVariable Long chipId) {
        // Initialization
        long fromTime = 0;
        String collectionName = String.valueOf(chipId);
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        StatsItem newItem = new StatsItem();
        // Load already calculated item from cache table
        List<StatsItem> items = mongoTemplate.find(Query.query(Criteria.where("sensorsTotal").ne(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        if(!items.isEmpty()) {
            newItem = items.get(0);
            fromTime = newItem.getTimestamp();
        }
        newItem.setChipId(chipId);

        // Sensor count
        newItem.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        newItem.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        // Records count
        newItem.setDataRecordsTotal(getRecordCountFromTimestamp(collectionName, 0, currentTime));
        newItem.setDataRecordsToday(getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime));
        newItem.setDataRecordsYesterday(getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]));
        newItem.setDataRecordsThisMonth(getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime));
        newItem.setDataRecordsPrevMonth(getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]));
        // Server request count
        newItem.setServerRequestsTotal(getServerRequestsCountFromTimestampSingleTotal(chipId, fromTime, currentTime));
        newItem.setServerRequestsTodayApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP, chipId, timestamps[1], currentTime));
        newItem.setServerRequestsTodayWebApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_WEB, chipId, timestamps[1], currentTime));
        newItem.setServerRequestsTodayGoogleActions(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_GA, chipId, timestamps[1], currentTime));
        newItem.setServerRequestsYesterdayApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP, chipId, timestamps[2], timestamps[1]));
        newItem.setServerRequestsYesterdayWebApp(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_WEB, chipId, timestamps[2], timestamps[1]));
        newItem.setServerRequestsYesterdayGoogleActions(getServerRequestsCountFromTimestampSingle(ConstantUtils.CLIENT_ID_PMAPP_GA, chipId, timestamps[2], timestamps[1]));

        // Save calculated values to caching table
        mongoTemplate.remove(Query.query(Criteria.where("chipId").is(chipId)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        mongoTemplate.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        return newItem;
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private long getRecordCountFromTimestamp(String collectionName, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to)).cursorBatchSize(500), collectionName);
    }

    private long getServerRequestsCountFromTimestamp(int clientId, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("clientId").is(clientId)).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampTotal(long from) {
        return mongoTemplate.count(new Query(Criteria.where("timestamp").gte(from)).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampSingle(int clientId, long chipId, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("target").is(chipId).and("clientId").is(clientId)).cursorBatchSize(100), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampSingleTotal(long chipId, long from, long to) {
        return mongoTemplate.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("target").is(chipId).and("clientId")).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);
    }

    private long[] calculateTimestamps(long currentTime) {
        long[] timestamps = new long[5];
        // Calculate timestamps
        timestamps[0] = currentTime - ConstantUtils.MINUTES_UNTIL_INACTIVITY;
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

    // ---------------------------------------------------- Cron jobs --------------------------------------------------

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    //@Scheduled(cron = "30 * * * * ?")
    public void calculateStats() {
        log.info("Calculating stats ...");
        // Initialization
        long fromTime = 0;
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        long recordsTotal, recordsYesterday, recordsThisMonth, recordsPrevMonth;
        recordsTotal = recordsYesterday = recordsThisMonth = recordsPrevMonth = 0;
        // Load already calculated item from cache table
        List<StatsItem> items = mongoTemplate.find(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        StatsItem newItem = new StatsItem();
        if(!items.isEmpty()) {
            newItem = items.get(0);
            fromTime = newItem.getTimestamp();
            recordsTotal = newItem.getDataRecordsTotal();
            recordsThisMonth = newItem.getDataRecordsThisMonth();
            recordsPrevMonth = newItem.getDataRecordsPrevMonth();
        }
        log.info("Finished reading old stats");

        // Calculate new stats
        Set<String> collectionNames = getDataCollections();
        // Calculate total records
        int collectionsSize = collectionNames.size();
        int currentCollection = 0;
        for(String collectionName : collectionNames) {
            recordsTotal += getRecordCountFromTimestamp(collectionName, fromTime, currentTime);
            recordsYesterday += getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]);
            recordsThisMonth += getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime);
            recordsPrevMonth += getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]);
            currentCollection++;
            log.info("Calculating " + currentCollection + " / " + collectionsSize + " (" + (currentCollection * 100 / collectionsSize) + " %)");
        }
        newItem.setDataRecordsTotal(recordsTotal);
        newItem.setDataRecordsYesterday(recordsYesterday);
        newItem.setDataRecordsThisMonth(recordsThisMonth);
        newItem.setDataRecordsPrevMonth(recordsPrevMonth);
        newItem.setServerRequestsTotal(newItem.getServerRequestsTotal() + getServerRequestsCountFromTimestampTotal(fromTime));
        newItem.setServerRequestsYesterdayApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP, timestamps[2], timestamps[1]));
        newItem.setServerRequestsYesterdayWebApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_WEB, timestamps[2], timestamps[1]));
        newItem.setServerRequestsYesterdayGoogleActions(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_GA, timestamps[2], timestamps[1]));

        // Save calculated values to caching table
        log.info("Saving them to the cache table ...");
        mongoTemplate.remove(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        mongoTemplate.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        log.info("Finished.");
    }

    private Set<String> getDataCollections() {
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        collectionNames.remove(ConstantUtils.LOG_TABLE_NAME);
        collectionNames.remove(ConstantUtils.STATS_TABLE_NAME);
        return collectionNames;
    }
}