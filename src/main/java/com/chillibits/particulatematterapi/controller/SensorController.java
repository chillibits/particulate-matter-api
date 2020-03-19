/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.io.MapsPlaceResult;
import com.chillibits.particulatematterapi.model.io.SyncPackage;
import com.chillibits.particulatematterapi.repository.main.SensorRepository;
import com.chillibits.particulatematterapi.shared.Constants;
import com.chillibits.particulatematterapi.shared.Credentials;
import com.chillibits.particulatematterapi.shared.Tools;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Sensor REST Endpoint", tags = "sensor")
public class SensorController {
    SensorRepository sensorRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sensor/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    public List<Sensor> getAllSensorsSync(@RequestBody SyncPackage syncPackage) {
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a sensor to the database")
    public Sensor addSensor(@RequestBody Sensor sensor) {
        // Check if sensor already exists
        if(sensorRepository.existsById(sensor.getChipId())) return null;
        // Retrieve country and city from latitude and longitude
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + Credentials.GOOGLE_API_KEY + "&latlng=" + sensor.getGpsLatitude() + "," + sensor.getGpsLongitude() + "&sensor=false&language=en";
            MapsPlaceResult place = new ObjectMapper().readValue(new URL(url), MapsPlaceResult.class);
            sensor.setCountry(place.getCountry());
            sensor.setCity(place.getCity());
        } catch (Exception e) {
            sensor.setCountry(Constants.BLANK_COLUMN);
            sensor.setCity(Constants.BLANK_COLUMN);
        }

        long currentTimestamp = System.currentTimeMillis();

        // Set remaining attributes
        sensor.setFirmwareVersion(Constants.EMPTY_COLUMN);
        sensor.setNotes(Constants.BLANK_COLUMN);
        sensor.setGpsLatitude(Tools.round(sensor.getGpsLatitude(), 4));
        sensor.setGpsLongitude(Tools.round(sensor.getGpsLongitude(), 4));
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
        return sensorRepository.updateSensor(sensor.getChipId(), sensor.getGpsLatitude(), sensor.getGpsLongitude(), sensor.getLastValueP1(), sensor.getLastValueP2());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/sensor/{id}")
    @ApiOperation(value = "Deletes a sensor from the database")
    public void deleteSensor(@PathVariable("id") Long id) {
        sensorRepository.deleteById(id);
    }
}