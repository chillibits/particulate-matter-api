/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.db.data.StatsItem;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
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
        // Retrieve stats
        long[] values = new long[5];
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        int i = 0;
        for(String collectionName : collectionNames) {
            values[0] += getRecordCountFromTimestamp(collectionName, 0, currentTime);
            values[1] += getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime);
            values[2] += getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]);
            values[3] += getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime);
            values[4] += getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]);
            i++;
            log.info(String.valueOf(i));
        }

        StatsItem stats = new StatsItem();
        // Sensor count
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        stats.setSensorsTotal(collectionNames.size());
        // Records count
        stats.setDataRecordsTotal(values[0]);
        stats.setDataRecordsToday(values[1]);
        stats.setDataRecordsYesterday(values[2]);
        stats.setDataRecordsThisMonth(values[3]);
        stats.setDataRecordsPrevMonth(values[4]);
        // Server request count
        //stats.setServerRequestsTotal(getServerRequestsCountFromTimestampTotal());
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
    public StatsItem getStatsOfSensor(@PathVariable Long chipId) {
        // Initialization
        String collectionName = String.valueOf(chipId);
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);

        StatsItem stats = new StatsItem();
        // Sensor count
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamps[0]));
        // Records count
        stats.setDataRecordsTotal(getRecordCountFromTimestamp(collectionName, 0, currentTime));
        stats.setDataRecordsToday(getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime));
        stats.setDataRecordsYesterday(getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]));
        stats.setDataRecordsThisMonth(getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime));
        stats.setDataRecordsPrevMonth(getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]));
        // Server request count
        //stats.setServerRequestsTotal(getServerRequestsCountFromTimestampSingleTotal(chipId, currentTime));
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

    //@Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    //@Scheduled(cron = "10 * * * * ?")
    public void calculateStats() {
        log.info("Calculating stats ...");
        // Initialization
        long fromTime = 0;
        long currentTime = 1541026800000L; //System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        long recordsTotal, recordsYesterday, recordsThisMonth, recordsPrevMonth, serverRequestsTotal,
                serverRequestsYesterdayApp, serverRequestsYesterdayWebApp, serverRequestsYesterdayGoogleActions;
        recordsTotal = recordsYesterday = recordsThisMonth = recordsPrevMonth = serverRequestsTotal = 0;
        // Get old values from caching table
        List<StatsItem> items = mongoTemplate.find(Query.query(Criteria.where("sensorsTotal").ne(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        StatsItem item = null;
        if(!items.isEmpty()) {
            item = items.get(0);
            // There is a cached stats item available
            fromTime = items.get(0).getTimestamp();
            recordsTotal = item.getDataRecordsTotal();
            recordsThisMonth = item.getDataRecordsThisMonth();
            recordsPrevMonth = item.getDataRecordsPrevMonth();
            serverRequestsTotal = item.getServerRequestsTotal();
        }
        log.info("Finished reading old stats");

        // Calculate new stats
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        CountOperation countOperation = Aggregation.count().as("count");
        ProjectionOperation projectionOperation = Aggregation.project("count");
        // Calculate total records
        MatchOperation matchOperation = Aggregation.match(Criteria.where("timestamp").gt(fromTime).lte(currentTime));
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, countOperation, projectionOperation);
        for(String collectionName : collectionNames) {
            AggregationResults<CountResult> aggregationResults = mongoTemplate.aggregate(aggregation, collectionName, CountResult.class);
            recordsTotal += aggregationResults.getMappedResults().get(0).count;
            log.info(String.valueOf(recordsTotal));
        }
        // Calculate records yesterday
        matchOperation = Aggregation.match(Criteria.where("timestamp").gt(timestamps[2]).lte(timestamps[1]));
        aggregation = Aggregation.newAggregation(matchOperation, countOperation, projectionOperation);
        for(String collectionName : collectionNames) {
            List<CountResult> aggregationResults = mongoTemplate.aggregate(aggregation, collectionName, CountResult.class).getMappedResults();
            recordsYesterday += aggregationResults.get(0).count;
            log.info(String.valueOf(recordsYesterday));
        }
        // Calculate records this month
        matchOperation = Aggregation.match(Criteria.where("timestamp").gt(timestamps[3]).lte(currentTime));
        aggregation = Aggregation.newAggregation(matchOperation, countOperation, projectionOperation);
        for(String collectionName : collectionNames) {
            AggregationResults<CountResult> aggregationResults = mongoTemplate.aggregate(aggregation, collectionName, CountResult.class);
            recordsThisMonth += aggregationResults.getMappedResults().get(0).count;
            log.info(String.valueOf(recordsThisMonth));
        }
        // Calculate records prev month
        if(true) {
            matchOperation = Aggregation.match(Criteria.where("timestamp").gt(timestamps[4]).lte(timestamps[3]));
            aggregation = Aggregation.newAggregation(matchOperation, countOperation, projectionOperation);
            for(String collectionName : collectionNames) {
                AggregationResults<CountResult> aggregationResults = mongoTemplate.aggregate(aggregation, collectionName, CountResult.class);
                recordsPrevMonth += aggregationResults.getMappedResults().get(0).count;
                log.info(String.valueOf(recordsPrevMonth));
            }
        }
        serverRequestsTotal += getServerRequestsCountFromTimestampTotal(fromTime);
        serverRequestsYesterdayApp = getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP, timestamps[2], timestamps[1]);
        serverRequestsYesterdayWebApp = getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_WEB, timestamps[2], timestamps[1]);
        serverRequestsYesterdayGoogleActions = getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_GA, timestamps[2], timestamps[1]);

        // Save calculated values to caching table
        log.info("Saving them to the cache table ...");
        StatsItem newItem;
        if(item != null) {
            newItem = new StatsItem(currentTime, item.getSensorsTotal(), item.getSensorsMapTotal(),
                    item.getSensorsMapActive(), serverRequestsTotal, 0,
                    0, 0, serverRequestsYesterdayApp,
                    serverRequestsYesterdayWebApp, serverRequestsYesterdayGoogleActions, recordsTotal, recordsThisMonth,
                    recordsPrevMonth, 0, recordsYesterday);
        } else {
            newItem = new StatsItem(currentTime, 0, 0, 0,
                    serverRequestsTotal, 0, 0, 0,
                    serverRequestsYesterdayApp, serverRequestsYesterdayWebApp, serverRequestsYesterdayGoogleActions,
                    recordsTotal, recordsThisMonth, recordsPrevMonth, 0, recordsYesterday);
        }
        mongoTemplate.remove(Query.query(Criteria.where("sensorTotal").ne(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        mongoTemplate.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        log.info("Finished.");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class CountResult {
        private long count;
    }
}