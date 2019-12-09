/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.controller;

import com.mrgames13.jimdo.particulatematterapp.model.Sensor;
import com.mrgames13.jimdo.particulatematterapp.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
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
