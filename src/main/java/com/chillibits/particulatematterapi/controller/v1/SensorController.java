/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.SensorCreationException;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.io.MapsPlaceResult;
import com.chillibits.particulatematterapi.model.io.SyncPackage;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.chillibits.particulatematterapi.shared.Credentials;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Sensor REST Endpoint", tags = "sensor")
public class SensorController {
    private SensorRepository sensorRepository;
    private UserRepository userRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    public List<Sensor> getAllSensors(
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(defaultValue = "0") int radius
    ) {
        if(radius == 0) return sensorRepository.findAll();
        return sensorRepository.findAllInRadius(latitude, longitude, radius);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sensor/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    public List<Sensor> getAllSensorsSync(@RequestBody SyncPackage syncPackage) {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a sensor to the database")
    public Sensor addSensor(@RequestBody Sensor sensor) throws SensorCreationException {
        // Check for possible faulty data parameters
        if(sensorRepository.existsById(sensor.getChipId())) throw new SensorCreationException(ErrorCodeUtils.SENSOR_ALREADY_EXISTS);
        if(sensor.getGpsLatitude() == 0 && sensor.getGpsLongitude() == 0) throw new SensorCreationException(ErrorCodeUtils.INVALID_GPS_COORDINATES);
        if(sensor.getGpsLatitude() == 200 && sensor.getGpsLongitude() == 200) throw new SensorCreationException(ErrorCodeUtils.INVALID_GPS_COORDINATES);
        if(!mongoTemplate.getCollectionNames().contains(String.valueOf(sensor.getChipId()))) throw new SensorCreationException(ErrorCodeUtils.NO_DATA_RECORDS);
        if(!userRepository.existsById(sensor.getUserId())) throw new SensorCreationException(ErrorCodeUtils.CANNOT_ASSIGN_TO_USER);

        // Retrieve country and city from latitude and longitude
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + Credentials.GOOGLE_API_KEY + "&latlng="
                    + sensor.getGpsLatitude() + "," + sensor.getGpsLongitude() + "&sensor=false&language=en";
            MapsPlaceResult place = new ObjectMapper().readValue(new URL(url), MapsPlaceResult.class);
            sensor.setCountry(place.getCountry());
            sensor.setCity(place.getCity());
        } catch (Exception e) {
            sensor.setCountry(ConstantUtils.BLANK_COLUMN);
            sensor.setCity(ConstantUtils.BLANK_COLUMN);
        }

        long currentTimestamp = System.currentTimeMillis();

        // Set remaining attributes
        sensor.setFirmwareVersion(ConstantUtils.EMPTY_COLUMN);
        sensor.setNotes(ConstantUtils.BLANK_COLUMN);
        sensor.setGpsLatitude(SharedUtils.round(sensor.getGpsLatitude(), 4));
        sensor.setGpsLongitude(SharedUtils.round(sensor.getGpsLongitude(), 4));
        sensor.setCreationTimestamp(currentTimestamp);
        sensor.setLastEditTimestamp(currentTimestamp);
        sensor.setMapsUrl("https://www.google.com/maps/place/" + sensor.getGpsLatitude() + "," + sensor.getGpsLongitude());
        sensor.setLastValueP1(0);
        sensor.setLastValueP2(0);

        // Save sensor to database
        return sensorRepository.save(sensor);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, path = "/sensor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a sensor")
    public Integer updateSensor(@RequestBody Sensor sensor) {
        return sensorRepository.updateSensor(
                sensor.getChipId(),
                sensor.getGpsLatitude(),
                sensor.getGpsLongitude(),
                sensor.getLastValueP1(),
                sensor.getLastValueP2()
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/sensor/{id}")
    @ApiOperation(value = "Deletes a sensor from the database")
    public void deleteSensor(@PathVariable("id") Long id) {
        sensorRepository.deleteById(id);
    }
}