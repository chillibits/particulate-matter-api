/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrgames13.jimdo.particulatematterapp.model.MapsPlaceResult;
import com.mrgames13.jimdo.particulatematterapp.model.Sensor;
import com.mrgames13.jimdo.particulatematterapp.repository.SensorRepository;
import com.mrgames13.jimdo.particulatematterapp.tool.Constants;
import com.mrgames13.jimdo.particulatematterapp.tool.Credentials;
import com.mrgames13.jimdo.particulatematterapp.tool.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.net.URL;
import java.util.List;

@RestController
public class SensorController {

    @Autowired
    SensorRepository sensorRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sensor> getAllSensors() {
        List<Sensor> sensorList = sensorRepository.findAll();
        return sensorList;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Sensor addSensor(@RequestBody Sensor sensor) {
        // Retrieve country and city from latitude and longitude
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + Credentials.GOOGLE_API_KEY + "&latlng=" + sensor.getLatitude() + "," + sensor.getLongitude() + "&sensor=false&language=de";
            System.out.println(url);
            MapsPlaceResult place = new ObjectMapper().readValue(new URL(url), MapsPlaceResult.class);
            sensor.setCountry(place.getCountry());
            sensor.setCity(place.getCity());
        } catch (Exception e) {
            e.printStackTrace();
            //TODO: Write to error log table
            sensor.setCountry(Constants.BLANK_COLUMN);
            sensor.setCity(Constants.BLANK_COLUMN);
        }

        // Set remaining attributes
        long creationDate = System.currentTimeMillis();
        sensor.setFirmwareVersion(Constants.EMPTY_COLUMN);
        sensor.setNotes(Constants.BLANK_COLUMN);
        sensor.setCreationDate(creationDate);
        sensor.setLatitude(Tools.round(sensor.getLatitude(), 4));
        sensor.setLongitude(Tools.round(sensor.getLongitude(), 4));
        sensor.setLastEdit(creationDate);
        sensor.setLastUpdate(creationDate);
        sensor.setMapsUrl("https://www.google.com/maps/place/" + sensor.getLatitude() + "," + sensor.getLongitude());
        sensor.setLastValueP1(0);
        sensor.setLastValueP2(0);

        // Save sensor to database
        return sensorRepository.save(sensor);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, path = "/sensor", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Integer updateSensor(@RequestBody Sensor sensor) {
        return sensorRepository.updateSensor(sensor.getId(), sensor.getLatitude(), sensor.getLongitude(), sensor.getLastValueP1(), sensor.getLastValueP2());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/sensor/{id}")
    public void deleteSensor(@PathVariable("id") Integer id) {
        sensorRepository.deleteById(id);
    }
}
