/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.io.DataRecordDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Api(value = "Data REST Endpoint", tags = "data")
public class DataController {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private SensorRepository sensorRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all data records for a specific sensor")
    public List<DataRecord> getDataRecordsUncompressed(
        @PathVariable long chipId,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) {
        return getDataRecords(chipId, from, to);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", params = "compressed")
    @ApiOperation(value = "Returns all data records for a specific sensor in a compressed form")
    public List<DataRecordDto> getDataRecordsCompressed(
        @PathVariable long chipId,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) {
        return getDataRecords(chipId, from, to).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the latest data record for a specific sensor")
    public DataRecord getLatestDataRecord(@PathVariable long chipId) {
        Query query = new Query().with(Sort.by(new Sort.Order(Sort.Direction.DESC, "timestamp"))).limit(1);
        return template.find(query, DataRecord.class, String.valueOf(chipId)).get(0);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all data records for a specific sensor", hidden = true)
    public List<DataRecord> getAllDataRecordsUncompressed(@PathVariable long chipId) {
        return template.find(Query.query(new Criteria()), DataRecord.class, String.valueOf(chipId));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}/all", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns all data records for a specific sensor", hidden = true)
    public List<DataRecordDto> getAllDataRecordsCompressed(@PathVariable long chipId) {
        return template.find(Query.query(new Criteria()), DataRecord.class, String.valueOf(chipId))
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/average_new", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a record with the averages of the latest values of the specified sensors")
    public DataRecordDto getDataAverageFromMultipleSensors(@RequestParam Long[] chipIds) {
        // Create Map for data values in schema: valueType, value, sensorCount
        Map<String, Map.Entry<Double, Integer>> dataValues = new HashMap<>();
        for(Long chipId : chipIds) {
            DataRecord currRecord = getLatestDataRecord(chipId);
            if(currRecord.getTimestamp() < System.currentTimeMillis() - ConstantUtils.MINUTES_UNTIL_INACTIVITY * 60 * 1000) continue;
            for(DataRecord.SensorDataValue currValue : currRecord.getSensorDataValues()) {
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
        avgRecord.setTimestamp(System.currentTimeMillis());
        avgRecord.setSensorDataValues(avgDataValues.toArray(DataRecord.SensorDataValue[]::new));
        return convertToDto(avgRecord);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/country/{country}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getDataCountry(
        @PathVariable String country,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to
    ) {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);

        // Get data from all selected sensors
        List<DataRecord> data = new ArrayList<>();
        for(Long chipId : chipIds) {
            for(DataRecord record : getDataRecords(chipId, from, to)) data.add(record);
        }

        return data;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/country/{country}/latest", produces = MediaType.APPLICATION_JSON_VALUE)
    public DataRecordDto getDataCountryLatest(@PathVariable String country) {
        // Get chipIds of the sensors from the requested location
        List<Long> chipIds = sensorRepository.getChipIdsOfSensorFromCountry(country);
        return getDataAverageFromMultipleSensors(chipIds.toArray(new Long[0]));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/chart")
    @ApiOperation(value = "Returns chart ready data", hidden = true)
    public String getChartData(
        @RequestParam(defaultValue = "3953497") long chipId,
        @RequestParam(defaultValue = "0") long from,
        @RequestParam(defaultValue = "0") long to,
        @RequestParam(defaultValue = "0") int fieldIndex
    ) {
        long startTimestamp = System.currentTimeMillis();
        JSONObject json = new JSONObject();
        List<DataRecord> records = getDataRecords(chipId, from, to);
        if(records.isEmpty()) return json.toString();
        JSONArray jsonTime = new JSONArray();
        JSONArray jsonValues = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat(from == 0 && to == 0 ? "HH:mm:ss" : "yyyy-MM-dd HH:mm:ss");
        records.forEach(record -> {
            jsonTime.put(sdf.format(record.getTimestamp()));
            jsonValues.put(record.getSensorDataValues()[fieldIndex].getValue());
        });
        json.put("field", records.get(0).getSensorDataValues()[fieldIndex].getValueType());
        json.put("time", jsonTime);
        json.put("values", jsonValues);
        long responseTime = System.currentTimeMillis() - startTimestamp;
        json.put("responseTime", responseTime);
        return json.toString();
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    public List<DataRecord> getDataRecords(long chipId, long from, long to) {
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIMESPAN : from;
        return template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp)), DataRecord.class, String.valueOf(chipId));
    }

    private DataRecordDto convertToDto(DataRecord record) {
        DataRecordDto dataRecordDto = mapper.map(record, DataRecordDto.class);
        dataRecordDto.setTimestamp(dataRecordDto.getTimestamp() / 1000);
        return dataRecordDto;
    }

    /*private DataRecord convertToEntity(DataRecordDto recordDto) {
        DataRecord record = mapper.map(recordDto, DataRecord.class);
        record.setTimestamp(record.getTimestamp() * 1000);
        return record;
    }*/
}