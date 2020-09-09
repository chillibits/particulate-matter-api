/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.DataAccessException;
import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.dto.DataRecordCompressedDto;
import com.chillibits.particulatematterapi.model.dto.DataRecordDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataService {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private SensorRepository sensorRepository;

    // -------------------------------------------- Data for single sensor ---------------------------------------------

    public List<DataRecordDto> getDataRecords(long chipId, long from, long to) throws DataAccessException {
        return getDataRecordsRaw(chipId, from, to).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<DataRecordCompressedDto> getDataRecordsCompressed(long chipId, long from, long to) throws DataAccessException {
        return getDataRecordsRaw(chipId, from, to).stream().map(this::convertToCompressedDto).collect(Collectors.toList());
    }

    public DataRecordDto getLatestDataRecord(long chipId) throws DataAccessException {
        Query query = new Query().with(Sort.by(new Sort.Order(Sort.Direction.DESC, "timestamp"))).limit(1);
        List<DataRecord> records = template.find(query, DataRecord.class, String.valueOf(chipId));
        return records == null || records.isEmpty() ? null : convertToDto(records.get(0)); // Do not remove records == null
    }

    public List<DataRecordCompressedDto> getAllDataRecordsCompressed(long chipId) throws DataAccessException {
        return getDataRecordsCompressed(chipId, 0, 0);
    }

    // ------------------------------------------ Data for multiple sensors --------------------------------------------

    public DataRecordDto getDataAverageMultipleSensors(List<Long> chipIds) throws DataAccessException {
        List<DataRecordDto> records = new ArrayList<>();
        for(long chipId : chipIds) {
            DataRecordDto latestRecord = getLatestDataRecord(chipId);
            if(latestRecord != null) records.add(latestRecord);
        }
        return getAverageDataRecord(records);
    }

    // ----------------------------------------------- Data for country ------------------------------------------------

    public List<DataRecordCompressedDto> getDataCountry(String country, long from, long to) throws DataAccessException {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);
        // Get data from all those sensors
        List<DataRecordCompressedDto> data = new ArrayList<>();
        for(long chipId : chipIds) data.addAll(getDataRecordsCompressed(chipId, from, to));
        Collections.sort(data);
        return data;
    }

    public DataRecordDto getDataCountryLatest(String country) throws DataAccessException {
        return getDataAverageMultipleSensors(sensorRepository.getChipIdsOfSensorFromCountry(country));
    }

    // ------------------------------------------------- Data for city -------------------------------------------------

    public List<DataRecordCompressedDto> getDataCity(String country, String city, long from, long to) throws DataAccessException {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCity(country, city);
        // Get data from all selected sensors
        List<DataRecordCompressedDto> data = new ArrayList<>();
        for(long chipId : chipIds) data.addAll(getDataRecordsCompressed(chipId, from, to));
        Collections.sort(data);
        return data;
    }

    public DataRecordDto getDataCityLatest(String country, String city) throws DataAccessException {
        return getDataAverageMultipleSensors(sensorRepository.getChipIdsOfSensorFromCity(country, city));
    }

    // --------------------------------------------- Chart data functions ----------------------------------------------

    public List<DataRecordDto> getChartData(long chipId, long from, long to, int fieldIndex, int mergeCount) throws DataAccessException {
        // Check input parameters
        if(fieldIndex < 0) throw new DataAccessException(ErrorCode.INVALID_FIELD_INDEX);
        if(mergeCount < 1) throw new DataAccessException(ErrorCode.INVALID_MERGE_COUNT);
        // Get data records of this sensor
        return getDataRecords(chipId, from, to);
    }

    public ImmutablePair<List<DataRecordDto>, Integer> getChartDataCountry(String country, long from, long to, int fieldIndex, int granularity) throws DataAccessException {
        // Check input parameters
        validateAccessProperties(from, to, fieldIndex, granularity);

        // Replace default values, with better ones
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);
        return new ImmutablePair<>(loopWithGranularity(granularity, toTimestamp, fromTimestamp, chipIds), chipIds.size());
    }

    public ImmutablePair<List<DataRecordDto>, Integer> getChartDataCity(String country, String city, long from, long to, int fieldIndex, int granularity) throws DataAccessException {
        // Check input parameters
        validateAccessProperties(from, to, fieldIndex, granularity);

        // Get replace default values, with better ones
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCity(country, city);
        return new ImmutablePair<>(loopWithGranularity(granularity, toTimestamp, fromTimestamp, chipIds), chipIds.size());
    }

    // ----------------------------------------------- Utility functions -----------------------------------------------

    private DataRecordDto getAverageDataRecord(List<DataRecordDto> records) {
        if(records.size() == 0) return new DataRecordDto();
        Map<String, Map.Entry<Double, Integer>> dataValues = new LinkedHashMap<>();
        for(DataRecordDto record : records) {
            for(DataRecordDto.SensorDataValue currValue : record.getSensorDataValues()) {
                String valueType = currValue.getValueType();
                if(dataValues.containsKey(valueType)) {
                    // Update map entry
                    Map.Entry<Double, Integer> currEntry = dataValues.get(valueType);
                    double currEntryValue = Double.parseDouble(currEntry.getKey().toString());
                    int currEntrySensorCount = Integer.parseInt(currEntry.getValue().toString()) +1;
                    dataValues.put(
                            valueType,
                            new AbstractMap.SimpleEntry<>(currEntryValue + (currValue.getValue() - currEntryValue) / currEntrySensorCount, currEntrySensorCount)
                    );
                } else {
                    // Add map entry
                    dataValues.put(valueType, new AbstractMap.SimpleEntry<>(currValue.getValue(), 1));
                }
            }
        }
        // Shrinking map entries
        ArrayList<DataRecordDto.SensorDataValue> avgDataValues = new ArrayList<>();
        for (Map.Entry<String, Map.Entry<Double, Integer>> item : dataValues.entrySet()) {
            double value = Double.parseDouble(item.getValue().getKey().toString());
            avgDataValues.add(new DataRecordDto.SensorDataValue(item.getKey(), SharedUtils.round(value, 3)));
        }
        // Create averageRecord out of dataValues
        DataRecordDto avgRecord = new DataRecordDto();
        avgRecord.setTimestamp(records.get(0).getTimestamp());
        avgRecord.setSensorDataValues(avgDataValues.toArray(DataRecordDto.SensorDataValue[]::new));
        return avgRecord;
    }

    private List<DataRecord> getDataRecordsRaw(long chipId, long from, long to) throws DataAccessException {
        if((from < 0 || to < 0) || (from > to)) throw new DataAccessException(ErrorCode.INVALID_TIME_RANGE_DATA);
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;
        List<DataRecord> records = template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp)).cursorBatchSize(500), DataRecord.class, String.valueOf(chipId));
        return records != null ? records : new ArrayList<>(); // Do not remove items != null
    }

    private List<DataRecordDto> loopWithGranularity(int granularity, long toTimestamp, long fromTimestamp, List<Long> chipIds) throws DataAccessException {
        // Loop periodically through the time span
        long granularityInMillis = granularity * 60 * 1000;
        long currFrom = fromTimestamp;
        long currTo = fromTimestamp + granularityInMillis;
        List<DataRecordDto> records = new ArrayList<>();
        while (currTo <= toTimestamp) {
            List<DataRecordDto> recordsInTimeSpan = new ArrayList<>();
            for (long chipId : chipIds) recordsInTimeSpan.addAll(getDataRecords(chipId, currFrom, currTo));
            records.add(getAverageDataRecord(recordsInTimeSpan));
            currFrom += granularityInMillis;
            currTo += granularityInMillis;
        }
        return records;
    }

    private void validateAccessProperties(long from, long to, int fieldIndex, int granularity) throws DataAccessException {
        if (from < 0 || to < 0) throw new DataAccessException(ErrorCode.INVALID_TIME_RANGE_DATA);
        if (fieldIndex < 0) throw new DataAccessException(ErrorCode.INVALID_FIELD_INDEX);
        if (granularity < 1) throw new DataAccessException(ErrorCode.INVALID_PERIOD);
    }

    private DataRecordDto convertToDto(DataRecord record) {
        return mapper.map(record, DataRecordDto.class);
    }

    private DataRecordCompressedDto convertToCompressedDto(DataRecord record) {
        DataRecordCompressedDto dataRecordDto = mapper.map(record, DataRecordCompressedDto.class);
        dataRecordDto.setTimestamp(dataRecordDto.getTimestamp() / 1000);
        return dataRecordDto;
    }
}