/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.StatsDataException;
import com.chillibits.particulatematterapi.model.db.data.StatsItem;
import com.chillibits.particulatematterapi.model.dto.StatsItemDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBar;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class StatsService {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;

    public StatsItemDto getAllStats() {
        // Initialization
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        // Load already calculated item from cache table
        List<StatsItem> items = template.find(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        StatsItem newItem = items.stream().findFirst().orElse(new StatsItem());
        // Retrieve newItem
        newItem.setTimestamp(currentTime);
        Set<String> collectionNames = getDataCollections();
        // Data records
        long dataRecordsToday = 0;
        for(String collectionName : collectionNames)
            dataRecordsToday += getRecordCountFromTimestamp(collectionName, timestamps[1], currentTime);
        newItem.setDataRecordsToday(dataRecordsToday);
        // Sensor count
        applySensorStats(newItem, collectionNames, timestamps[0]);
        // Server request count
        newItem.setServerRequestsTodayApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP, timestamps[1], currentTime));
        newItem.setServerRequestsTodayWebApp(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_WEB, timestamps[1], currentTime));
        newItem.setServerRequestsTodayGoogleActions(getServerRequestsCountFromTimestamp(ConstantUtils.CLIENT_ID_PMAPP_GA, timestamps[1], currentTime));

        // Save calculated values to caching table
        template.remove(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        template.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        return convertToDto(newItem);
    }

    public StatsItemDto getStatsBySensor(long chipId) throws StatsDataException {
        // Check if sensor is existing
        Set<String> collectionNames = getDataCollections();
        String collectionName = String.valueOf(chipId);
        if(!collectionNames.contains(collectionName)) throw new StatsDataException(ErrorCode.STATS_ITEM_DOES_NOT_EXIST);
        // Initialization
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        // Load already calculated item from cache table
        StatsItem newItem = template.find(Query.query(Criteria.where("chipId").is(chipId)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME).stream().findFirst().orElse(new StatsItem());
        long fromTime = newItem.getChipId() != 0L ? newItem.getTimestamp() : 0;
        newItem.setChipId(chipId);
        newItem.setTimestamp(currentTime);

        // Sensor count
        applySensorStats(newItem, collectionNames, timestamps[0]);
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
        template.remove(Query.query(Criteria.where("chipId").is(chipId)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        template.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        return convertToDto(newItem);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private long getRecordCountFromTimestamp(String collectionName, long from, long to) {
        return template.count(Query.query(Criteria.where("timestamp").gte(from).lte(to)).cursorBatchSize(500), collectionName);
    }

    private long getServerRequestsCountFromTimestamp(int clientId, long from, long to) {
        return template.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("clientId").is(clientId)).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampTotal(long from) {
        return template.count(new Query(Criteria.where("timestamp").gte(from)).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampSingle(int clientId, long chipId, long from, long to) {
        return template.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("target").is(chipId).and("clientId").is(clientId)).cursorBatchSize(100), ConstantUtils.LOG_TABLE_NAME);
    }

    private long getServerRequestsCountFromTimestampSingleTotal(long chipId, long from, long to) {
        return template.count(Query.query(Criteria.where("timestamp").gte(from).lte(to).and("target").is(chipId).and("clientId")).cursorBatchSize(500), ConstantUtils.LOG_TABLE_NAME);
    }

    private void applySensorStats(StatsItem newItem, Set<String> collectionNames, long timestamp) {
        newItem.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        newItem.setSensorsMapActive(sensorRepository.getSensorsMapActive(timestamp));
        newItem.setSensorsTotal(collectionNames.size());
    }

    public long[] calculateTimestamps(long currentTime) {
        long[] timestamps = new long[5];
        // Calculate timestamps
        timestamps[0] = currentTime - ConstantUtils.MINUTES_UNTIL_INACTIVITY;
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(currentTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        timestamps[1] = cal.getTimeInMillis(); // Midnight today
        timestamps[2] = timestamps[1] - 24 * 60 * 60 * 1000; // Midnight yesterday
        cal.set(Calendar.DAY_OF_MONTH, 1);
        timestamps[3] = cal.getTimeInMillis(); // Midnight 1st of this month
        cal.add(Calendar.MONTH, -1);
        timestamps[4] = cal.getTimeInMillis(); // Midnight 1st of previous month
        return timestamps;
    }

    private Set<String> getDataCollections() {
        Set<String> collectionNames = template.getCollectionNames();
        collectionNames.remove(ConstantUtils.LOG_TABLE_NAME);
        collectionNames.remove(ConstantUtils.STATS_TABLE_NAME);
        return collectionNames;
    }

    private StatsItemDto convertToDto(StatsItem statsItem) {
        return mapper.map(statsItem, StatsItemDto.class);
    }

    // ---------------------------------------------------- Cron jobs --------------------------------------------------

    @PostConstruct
    public void onStartup() {
        if(ConstantUtils.CALC_STATS_ON_STARTUP) calculateStats();
    }

    @Scheduled(cron = "0 0 0 * * ?") // Schedule cron job every day at midnight
    public void calculateStats() {
        log.info("Calculating stats ...");
        // Initialization
        long fromTime = 0;
        long currentTime = System.currentTimeMillis();
        long[] timestamps = calculateTimestamps(currentTime);
        long recordsTotal, recordsYesterday, recordsThisMonth, recordsPrevMonth;
        recordsTotal = recordsYesterday = recordsThisMonth = recordsPrevMonth = 0;
        // Load already calculated item from cache table
        List<StatsItem> items = template.find(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
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
        for(String collectionName : ProgressBar.wrap(collectionNames, "Calculating Stats")) {
            recordsTotal += getRecordCountFromTimestamp(collectionName, fromTime, currentTime);
            recordsYesterday += getRecordCountFromTimestamp(collectionName, timestamps[2], timestamps[1]);
            recordsThisMonth += getRecordCountFromTimestamp(collectionName, timestamps[3], currentTime);
            recordsPrevMonth += getRecordCountFromTimestamp(collectionName, timestamps[4], timestamps[3]);
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
        template.remove(Query.query(Criteria.where("chipId").is(0)).limit(1), StatsItem.class, ConstantUtils.STATS_TABLE_NAME);
        template.save(newItem, ConstantUtils.STATS_TABLE_NAME);

        log.info("Finished.");
    }
}