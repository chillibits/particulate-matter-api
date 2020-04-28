/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.SensorDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.SensorCompressedDto;
import com.chillibits.particulatematterapi.model.dto.SensorDto;
import com.chillibits.particulatematterapi.model.io.MapsPlaceResult;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.chillibits.particulatematterapi.shared.Credentials;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "Sensor REST Endpoint", tags = "sensor")
public class SensorController {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ModelMapper mapper;

    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    public List<SensorDto> getAllSensors(
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(defaultValue = "0") int radius,
            @RequestParam(defaultValue = "true") boolean onlyPublished
    ) throws SensorDataException {
        List<Sensor> sensors = getSensors(latitude, longitude, radius, onlyPublished);
        return sensors.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns all sensors, registered in the database in a compressed form")
    public List<SensorCompressedDto> getAllSensorsCompressed(
            @RequestParam(defaultValue = "0") double latitude,
            @RequestParam(defaultValue = "0") double longitude,
            @RequestParam(defaultValue = "0") int radius,
            @RequestParam(defaultValue = "true") boolean onlyPublished
    ) throws SensorDataException {
        List<Sensor> sensors = getSensors(latitude, longitude, radius, onlyPublished);
        System.out.println(sensors.toString());
        return sensors.stream().map(this::convertToCompressedDto).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sensor/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns info for a specific sensor")
    public SensorDto getSingleSensor(@PathVariable long chipId) {
        return sensorRepository.findById(chipId).map(this::convertToDto).orElse(null);
    }

    /*@RequestMapping(method = RequestMethod.GET, path = "/sensor/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all sensors, registered in the database")
    public List<Sensor> getAllSensorsSync(@RequestBody SyncPackage syncPackage) {
        return null;
    }*/

    @RequestMapping(method = RequestMethod.POST, path = "/sensor", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a sensor to the database")
    public Sensor addSensor(@RequestBody Sensor sensor) throws SensorDataException {
        // Check for possible faulty data parameters
        if(sensorRepository.existsById(sensor.getChipId())) throw new SensorDataException(ErrorCodeUtils.SENSOR_ALREADY_EXISTS);
        if(!mongoTemplate.getCollectionNames().contains(String.valueOf(sensor.getChipId()))) throw new SensorDataException(ErrorCodeUtils.NO_DATA_RECORDS);
        User user = validateSensorObject(sensor);

        long currentTimestamp = System.currentTimeMillis();

        // Set remaining attributes
        retrieveCountryCityFromCoordinates(sensor);
        sensor.setFirmwareVersion(ConstantUtils.EMPTY_COLUMN);
        sensor.setNotes(ConstantUtils.BLANK_COLUMN);
        sensor.setGpsLatitude(SharedUtils.round(sensor.getGpsLatitude(), 4));
        sensor.setGpsLongitude(SharedUtils.round(sensor.getGpsLongitude(), 4));
        sensor.setCreationTimestamp(currentTimestamp);
        sensor.setLastEditTimestamp(currentTimestamp);

        // Save sensor to database
        Sensor createdSensor = sensorRepository.save(sensor);

        // Save UserSensorLink to the database
        Link link = new Link();
        link.setUser(user);
        link.setSensor(sensor);
        link.setCreationTimestamp(currentTimestamp);
        linkRepository.save(link);

        return createdSensor;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/sensor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a sensor")
    public Integer updateSensor(@RequestBody Sensor sensor) throws SensorDataException {
        // Check for possible faulty data parameters
        if(!sensorRepository.existsById(sensor.getChipId())) throw new SensorDataException(ErrorCodeUtils.SENSOR_NOT_EXISTING);
        validateSensorObject(sensor);

        retrieveCountryCityFromCoordinates(sensor);
        return sensorRepository.updateSensor(
                sensor.getChipId(),
                sensor.getGpsLatitude(),
                sensor.getGpsLongitude(),
                sensor.getCountry(),
                sensor.getCity(),
                System.currentTimeMillis(),
                sensor.getNotes(),
                sensor.isIndoor(),
                sensor.isPublished()
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/sensor/{id}")
    @ApiOperation(value = "Deletes a sensor from the database")
    public void deleteSensor(@PathVariable("id") Long id) {
        sensorRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private List<Sensor> getSensors(double latitude, double longitude, int radius, boolean onlyPublished) throws SensorDataException {
        if (radius < 0) throw new SensorDataException(ErrorCodeUtils.INVALID_RADIUS);
        List<Sensor> sensors;
        if (onlyPublished) {
            if (radius == 0)
                sensors = sensorRepository.findAllPublished();
            else
                sensors = sensorRepository.findAllPublishedInRadius(latitude, longitude, radius);
        } else {
            if (radius == 0)
                sensors = sensorRepository.findAll();
            else
                sensors = sensorRepository.findAllInRadius(latitude, longitude, radius);
        }
        sensors.forEach(sensor ->
                sensor.setActive(sensor.getLastMeasurementTimestamp() > System.currentTimeMillis() -
                        ConstantUtils.MINUTES_UNTIL_INACTIVITY * 60 * 1000)
        );
        return sensors;
    }

    private void retrieveCountryCityFromCoordinates(Sensor sensor) {
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
    }

    private SensorDto convertToDto(Sensor sensor) {
        return mapper.map(sensor, SensorDto.class);
    }

    private SensorCompressedDto convertToCompressedDto(Sensor sensor) {
        return mapper.map(sensor, SensorCompressedDto.class);
    }

    private User validateSensorObject(Sensor sensor) throws SensorDataException {
        // Extract requesting user
        User user = Arrays.asList(sensor.getUserLinks().toArray(new Link[0])).get(0).user;
        if(!userRepository.existsById(user.getId())) throw new SensorDataException(ErrorCodeUtils.CANNOT_ASSIGN_TO_USER);
        if(sensor.getGpsLatitude() == 0 && sensor.getGpsLongitude() == 0) throw new SensorDataException(ErrorCodeUtils.INVALID_GPS_COORDINATES);
        if(sensor.getGpsLatitude() == 200 && sensor.getGpsLongitude() == 200) throw new SensorDataException(ErrorCodeUtils.INVALID_GPS_COORDINATES);
        return user;
    }
}