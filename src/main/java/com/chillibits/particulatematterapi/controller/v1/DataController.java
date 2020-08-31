/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.DataAccessException;
import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.dto.DataRecordDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Api(value = "Data REST Endpoint", tags = "data")
public class DataController {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private SensorRepository sensorRepository;

    // ------------------------------------------- Data for single sensor ----------------------------------------------

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all data records for a specific sensor")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<DataRecord> getDataRecordsUncompressed(
        @PathVariable long chipId,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) throws DataAccessException {
        return getDataRecords(chipId, from, to);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", params = "compressed")
    @ApiOperation(value = "Returns all data records for a specific sensor in a compressed form")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<DataRecordDto> getDataRecordsCompressed(
        @PathVariable long chipId,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) throws DataAccessException {
        return getDataRecords(chipId, from, to).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the latest data record for a specific sensor")
    public DataRecord getLatestDataRecord(@PathVariable long chipId) {
        Query query = new Query().with(Sort.by(new Sort.Order(Sort.Direction.DESC, "timestamp"))).limit(1);
        List<DataRecord> records = template.find(query, DataRecord.class, String.valueOf(chipId));
        return records.size() > 0 ? records.get(0) : null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}/all", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns all data records for a specific sensor", hidden = true)
    public List<DataRecordDto> getAllDataRecordsCompressed(@PathVariable long chipId) {
        return template.find(Query.query(new Criteria()).cursorBatchSize(500), DataRecord.class, String.valueOf(chipId))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------- Data for multiple sensors ---------------------------------------

    @RequestMapping(method = RequestMethod.GET, path = "/data/average", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a record with the averages of the latest values of the specified sensors")
    public DataRecordDto getDataAverageFromMultipleSensors(@RequestParam Long[] chipIds) {
        List<DataRecord> records = new ArrayList<>();
        for(Long chipId : chipIds) {
            DataRecord latestRecord = getLatestDataRecord(chipId);
            if(latestRecord != null) records.add(latestRecord);
        }
        return convertToDto(getAverageDataRecord(records));
    }

    // --------------------------------------------------- Data for country --------------------------------------------

    @RequestMapping(method = RequestMethod.GET, path = "/data/country/{country}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all data records from sensors in a specific country")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<DataRecord> getDataCountry(
        @PathVariable String country,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) throws DataAccessException {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);

        // Get data from all selected sensors
        List<DataRecord> data = new ArrayList<>();
        for(Long chipId : chipIds) data.addAll(getDataRecords(chipId, from, to));
        return data;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/country/{country}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the latest data record for a specific country")
    public DataRecordDto getDataCountryLatest(
        @PathVariable String country
    ) {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);
        // Get average from these sensors
        return getDataAverageFromMultipleSensors(chipIds.toArray(new Long[0]));
    }

    // ---------------------------------------------------- Data for city ----------------------------------------------

    @RequestMapping(method = RequestMethod.GET, path = "/data/city/{country}/{city}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all data records from sensors in a specific city")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<DataRecord> getDataCity(
        @PathVariable String country,
        @PathVariable String city,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) throws DataAccessException {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCity(country, city);

        long startTime = System.currentTimeMillis();
        // Get data from all selected sensors
        List<DataRecord> data = new ArrayList<>();
        for(Long chipId : chipIds) data.addAll(getDataRecords(chipId, from, to));
        log.info("LoadData: " + (System.currentTimeMillis() - startTime));
        return data;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/city/{country}/{city}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the latest data record for a specific city")
    public DataRecordDto getDataCityLatest(
        @PathVariable String country,
        @PathVariable String city
    ) {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCity(country, city);
        // Get average from these sensors
        return getDataAverageFromMultipleSensors(chipIds.toArray(new Long[0]));
    }

    // --------------------------------------------- Chart data functions ----------------------------------------------

    @RequestMapping(method = RequestMethod.GET, path = "/data/chart")
    @ApiOperation(value = "Returns chart ready data", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0"),
            @ApiResponse(code = 201, message = "Invalid merge count. Must be >= 1"),
            @ApiResponse(code = 201, message = "Invalid field index. Please provide a number >= 0. Also make sure, it's not too high.")
    })
    public String getChartData(
        @RequestParam long chipId,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to,
        @RequestParam(defaultValue = "0") int fieldIndex,
        @RequestParam(defaultValue = "1") int mergeCount
    ) throws DataAccessException {
        // Check input parameters
        if(fieldIndex < 0) throw new DataAccessException(ErrorCodeUtils.INVALID_FIELD_INDEX);
        if(mergeCount < 1) throw new DataAccessException(ErrorCodeUtils.INVALID_MERGE_COUNT);

        long startTimestamp = System.currentTimeMillis();
        // Get data records of this sensor
        List<DataRecord> records = getDataRecords(chipId, from, to);
        return chartDataToJson(fieldIndex, startTimestamp, records, 1);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/chart", params = "country")
    @ApiOperation(value = "Returns chart ready data for a specific country", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0"),
            @ApiResponse(code = 201, message = "Invalid period. Please provide a period >= 1"),
            @ApiResponse(code = 201, message = "Invalid field index. Please provide a number >= 0. Also make sure, it's not too high.")
    })
    public String getChartDataCountry(
            @RequestParam String country,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "60") int granularity  // in minutes
    ) throws DataAccessException {
        // Check input parameters
        validateAccessProperties(from, to, fieldIndex, granularity);

        long startTimestamp = System.currentTimeMillis();
        // Get replace default values, with better ones
        long toTimestamp = to == 0 ? startTimestamp : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);
        List<DataRecord> records = loopWithGranularity(granularity, toTimestamp, fromTimestamp, chipIds);

        // Bring them into json format
        return chartDataToJson(fieldIndex, startTimestamp, records, chipIds.size());
    }

    private List<DataRecord> loopWithGranularity(@RequestParam(defaultValue = "60") int granularity, long toTimestamp, long fromTimestamp, List<Long> chipIds) throws DataAccessException {
        // Loop periodically through the time span
        long granularityInMillis = granularity * 60 * 1000;
        long currFrom = fromTimestamp;
        long currTo = fromTimestamp + granularityInMillis;
        List<DataRecord> records = new ArrayList<>();
        while (currTo <= toTimestamp) {
            List<DataRecord> recordsInTimeSpan = new ArrayList<>();
            for (Long chipId : chipIds) recordsInTimeSpan.addAll(getDataRecords(chipId, currFrom, currTo));
            records.add(getAverageDataRecord(recordsInTimeSpan));
            currFrom += granularityInMillis;
            currTo += granularityInMillis;
        }
        return records;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/chart", params = {"country", "city"})
    @ApiOperation(value = "Returns chart ready data for a specific city", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0"),
            @ApiResponse(code = 201, message = "Invalid period. Please provide a period >= 1"),
            @ApiResponse(code = 201, message = "Invalid field index. Please provide a number >= 0. Also make sure, it's not too high.")
    })
    public String getChartDataCity(
            @RequestParam String country,
            @RequestParam String city,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "0") int fieldIndex,
            @RequestParam(defaultValue = "60") int granularity  // in minutes
    ) throws DataAccessException {
        // Check input parameters
        validateAccessProperties(from, to, fieldIndex, granularity);

        long startTimestamp = System.currentTimeMillis();
        // Get replace default values, with better ones
        long toTimestamp = to == 0 ? startTimestamp : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCity(country, city);

        // Loop periodically through the time span
        List<DataRecord> records = loopWithGranularity(granularity, toTimestamp, fromTimestamp, chipIds);
        // Bring them into json format
        return chartDataToJson(fieldIndex, startTimestamp, records, chipIds.size());
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private DataRecord getAverageDataRecord(List<DataRecord> records) {
        if(records.size() == 0) return new DataRecord();
        long startTime = System.currentTimeMillis();
        Map<String, Map.Entry<Double, Integer>> dataValues = new LinkedHashMap<>();
        for(DataRecord record : records) {
            for(DataRecord.SensorDataValue currValue : record.getSensorDataValues()) {
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
        ArrayList<DataRecord.SensorDataValue> avgDataValues = new ArrayList<>();
        for (Map.Entry<String, Map.Entry<Double, Integer>> item : dataValues.entrySet()) {
            double value = Double.parseDouble(item.getValue().getKey().toString());
            avgDataValues.add(new DataRecord.SensorDataValue(item.getKey(), SharedUtils.round(value, 3)));
        }
        // Create averageRecord out of dataValues
        DataRecord avgRecord = new DataRecord();
        avgRecord.setTimestamp(records.get(0).getTimestamp());
        avgRecord.setSensorDataValues(avgDataValues.toArray(DataRecord.SensorDataValue[]::new));
        log.info("Average: " + (System.currentTimeMillis() - startTime));
        return avgRecord;
    }

    private List<DataRecord> getDataRecords(long chipId, long from, long to) throws DataAccessException {
        if(from < 0 || to < 0) throw new DataAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_DATA);
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;
        return template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp)).cursorBatchSize(500), DataRecord.class, String.valueOf(chipId));
    }

    private String chartDataToJson(int fieldIndex, long startTimestamp, List<DataRecord> records, int sensorCount) throws DataAccessException {
        JSONObject json = new JSONObject();
        // Handle possible errors
        if(!records.isEmpty()) {
            if(fieldIndex >= records.get(0).getSensorDataValues().length) throw new DataAccessException(ErrorCodeUtils.INVALID_FIELD_INDEX);
            // Bring the records into json format
            JSONArray jsonValues = new JSONArray();
            records.forEach(record -> {
                try {
                    JSONArray recordObject = new JSONArray();
                    recordObject.put(record.getTimestamp());
                    recordObject.put(record.getSensorDataValues()[fieldIndex].getValue());
                    jsonValues.put(recordObject);
                } catch (Exception ignored) {}
            });
            json.put("values", jsonValues);
            json.put("field", records.get(0).getSensorDataValues()[fieldIndex].getValueType());
        }
        long responseTime = System.currentTimeMillis() - startTimestamp;
        json.put("responseTime", responseTime);
        json.put("sensorCount", sensorCount);
        return json.toString();
    }

    private void validateAccessProperties(long from, long to, int fieldIndex, int granularity) throws DataAccessException {
        if (from < 0 || to < 0) throw new DataAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_DATA);
        if (fieldIndex < 0) throw new DataAccessException(ErrorCodeUtils.INVALID_FIELD_INDEX);
        if (granularity < 1) throw new DataAccessException(ErrorCodeUtils.INVALID_PERIOD);
    }

    private DataRecordDto convertToDto(DataRecord record) {
        DataRecordDto dataRecordDto = mapper.map(record, DataRecordDto.class);
        dataRecordDto.setTimestamp(dataRecordDto.getTimestamp() / 1000);
        return dataRecordDto;
    }
}