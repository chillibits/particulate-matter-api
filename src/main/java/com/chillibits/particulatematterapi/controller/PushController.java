/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Api(value = "Push REST Endpoint", tags = "push")
public class PushController {
    private SensorRepository sensorRepository;
    private MongoTemplate template;

    @RequestMapping(method = RequestMethod.POST, path = "/push", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Pushes a measurement record to the database")
    public String pushData(@RequestBody DataRecord record) {
        long timestamp = System.currentTimeMillis();
        record.setTimestamp(timestamp);
        // Update Sensor record
        sensorRepository.findById(record.getChipId()).ifPresent(sensor -> {
            // Update live-properties
            sensor.setLastMeasurementTimestamp(timestamp);
            sensor.setFirmwareVersion(record.getFirmwareVersion());
            // Set gps coordinates, if they were passed
            Optional<DataRecord.SensorDataValues> pairLat = Arrays.stream(record.getSensorDataValues()).filter(keyValuePair -> keyValuePair.getValueType().equals("GPS_lat")).findAny();
            Optional<DataRecord.SensorDataValues> pairLng = Arrays.stream(record.getSensorDataValues()).filter(keyValuePair -> keyValuePair.getValueType().equals("GPS_lng")).findAny();
            Optional<DataRecord.SensorDataValues> pairAlt = Arrays.stream(record.getSensorDataValues()).filter(keyValuePair -> keyValuePair.getValueType().equals("GPS_height")).findAny();
            if(pairLat.isPresent() && pairLng.isPresent() && pairAlt.isPresent()) {
                sensor.setGpsLatitude(pairLat.get().getValue());
                sensor.setGpsLongitude(pairLng.get().getValue());
                sensor.setGpsAltitude(pairAlt.get().getValue());
            }
            // Save to db
            sensorRepository.save(sensor);
        });
        // Save record to data db
        template.save(record, String.valueOf(record.getChipId()));
        return "ok";
    }
}