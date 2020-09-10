/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.dto.SensorCompressedDto;
import com.chillibits.particulatematterapi.model.dto.SensorDto;
import com.chillibits.particulatematterapi.model.dto.SensorInsertUpdateDto;
import com.chillibits.particulatematterapi.service.SensorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Sensor endpoint
 *
 * Main endpoint for adding, updating or deleting sensors
 */
@RestController
@Api(value = "Sensor REST Endpoint", tags = "sensor")
public class SensorController {

    @Autowired
    private SensorService sensorService;

    /**
     * Returns all sensors, registered in the database
     *
     *
     * @param latitude Latitude of location filter
     * @param longitude Longitude of location filter
     * @param radius Radius of the location filter (in meters)
     * @param onlyPublished Boolean (true: only published, false: all sensors)
     * @return List of sensor records as List of SensorDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid radius. Please provide a radius >= 0"),
            @ApiResponse(code = 406, message = "Invalid gps coordinates.")
    })
    public List<SensorDto> getAllSensors(
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(defaultValue = "0") int radius,
            @RequestParam(defaultValue = "true") boolean onlyPublished
    ) {
        return sensorService.getAllSensors(latitude, longitude, radius, onlyPublished);
    }

    /**
     * Returns all sensors, registered in the database in a compressed form
     *
     * @param latitude Latitude of location filter
     * @param longitude Longitude of location filter
     * @param radius Radius of the location filter (in meters)
     * @param onlyPublished Boolean (true: only published, false: all sensors)
     * @return List of sensor records as List of SensorCompressedDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns all sensors, registered in the database in a compressed form")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid radius. Please provide a radius >= 0"),
            @ApiResponse(code = 406, message = "Invalid gps coordinates.")
    })
    public List<SensorCompressedDto> getAllSensorsCompressed(
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(defaultValue = "0") int radius,
            @RequestParam(defaultValue = "true") boolean onlyPublished
    ) {
        return sensorService.getAllSensorsCompressed(latitude, longitude, radius, onlyPublished);
    }

    /**
     * Returns info for a specific sensor
     *
     * @param chipId Chip-ID of requested sensor
     * @return Sensor info as SensorDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/sensor/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns info for a specific sensor")
    public SensorDto getSingleSensor(@PathVariable long chipId) {
        return sensorService.getSingleSensor(chipId);
    }

    /**
     * Adds a sensor to the database
     * <p>Note: Requires application role A (usual application)</p>
     *
     * @param sensor Instance of SensorInsertUpdateDto with all required data values
     * @return Inserted sensor record as SensorDto
     */
    @RequestMapping(method = RequestMethod.POST, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a sensor to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "The sensor with this chip id already exists in the database."),
            @ApiResponse(code = 406, message = "Cannot create a sensor without having received at least one data record from it."),
            @ApiResponse(code = 406, message = "Invalid gps coordinates."),
            @ApiResponse(code = 406, message = "You cannot assign a sensor to a non-existing user.")
    })
    public SensorDto addSensor(@RequestBody SensorInsertUpdateDto sensor) {
        return sensorService.addSensor(sensor);
    }

    /**
     * Updates a sensor
     * <p>Note: Requires application role A (usual application)</p>
     *
     * @param sensor Updated sensor item as SensorInsertUpdateDto
     * @return Status code of update transaction
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/sensor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a sensor")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Cannot update a non-existing sensor."),
            @ApiResponse(code = 406, message = "Invalid gps coordinates."),
            @ApiResponse(code = 406, message = "You cannot assign a sensor to a non-existing user.")
    })
    public Integer updateSensor(@RequestBody SensorInsertUpdateDto sensor) {
        return sensorService.updateSensor(sensor);
    }

    /**
     * Deletes a sensor from the database
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @param id Chip-Id of the sensor, which has to be deleted
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/sensor/{id}")
    @ApiOperation(value = "Deletes a sensor from the database")
    public void deleteSensor(@PathVariable("id") long id) {
        sensorService.deleteSensorByChipId(id);
    }
}