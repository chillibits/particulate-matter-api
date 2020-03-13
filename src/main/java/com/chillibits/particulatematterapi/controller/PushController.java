package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.DataRecord;
import com.chillibits.particulatematterapi.repository.main.SensorRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(value = "Push REST Endpoint", tags = { "push" })
public class PushController {
    SensorRepository sensorRepository;
    MongoOperations operations;

    @RequestMapping(method = RequestMethod.POST, path = "/push", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Pushes a measurement record to the database")
    public String pushData(@RequestBody DataRecord record) {
        long timestamp = System.currentTimeMillis();
        // Update Sensor record
        sensorRepository.findById(record.getChipId()).ifPresent(sensor -> {
            sensor.setLastMeasurementTimestamp(timestamp);
            sensor.setFirmwareVersion(record.getFirmwareVersion());
            sensorRepository.save(sensor);
        });
        // Save record to db
        operations.save(record, String.valueOf(record.getChipId()));
        return "ok";
    }
}