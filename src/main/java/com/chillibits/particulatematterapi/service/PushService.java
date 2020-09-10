/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.PushDataException;
import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.dto.DataRecordInsertUpdateDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class PushService {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;

    public boolean pushData(DataRecordInsertUpdateDto record) throws PushDataException {
        // Check if the record contains data values
        if(record.getSensorDataValues() == null || record.getSensorDataValues().length == 0)
            throw new PushDataException(ErrorCode.NO_DATA_VALUES);

        long timestamp = System.currentTimeMillis();
        record.setTimestamp(timestamp);
        // Update Sensor record, if exists
        sensorRepository.findById(record.getChipId()).ifPresent(sensor -> {
            // Update live-properties
            sensor.setLastMeasurementTimestamp(timestamp);
            sensor.setFirmwareVersion(record.getFirmwareVersion());
            // Set gps coordinates, if they were passed and valid
            Optional<DataRecordInsertUpdateDto.SensorDataValue> pairLat
                    = Arrays.stream(record.getSensorDataValues()).filter(keyValuePair -> keyValuePair.getValueType().equals("GPS_lat")).findAny();
            Optional<DataRecordInsertUpdateDto.SensorDataValue> pairLng
                    = Arrays.stream(record.getSensorDataValues()).filter(keyValuePair -> keyValuePair.getValueType().equals("GPS_lng")).findAny();
            Optional<DataRecordInsertUpdateDto.SensorDataValue> pairAlt
                    = Arrays.stream(record.getSensorDataValues()).filter(keyValuePair -> keyValuePair.getValueType().equals("GPS_height")).findAny();
            if(pairLat.isPresent() && pairLng.isPresent() && pairAlt.isPresent() && pairLat.get().getValue() != -200) {
                sensor.setGpsLatitude(pairLat.get().getValue());
                sensor.setGpsLongitude(pairLng.get().getValue());
                sensor.setGpsAltitude((int) Math.round(pairAlt.get().getValue()));
            }
            // Save to db
            sensorRepository.save(sensor);
        });
        // Save record to data db
        template.save(convertToDbo(record), String.valueOf(record.getChipId()));
        return true;
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private DataRecord convertToDbo(DataRecordInsertUpdateDto record) {
        return mapper.map(record, DataRecord.class);
    }
}