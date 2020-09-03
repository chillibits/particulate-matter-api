/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.SensorDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dto.LinkInsertUpdateDto;
import com.chillibits.particulatematterapi.model.dto.SensorCompressedDto;
import com.chillibits.particulatematterapi.model.dto.SensorDto;
import com.chillibits.particulatematterapi.model.dto.SensorInsertUpdateDto;
import com.chillibits.particulatematterapi.model.io.MapsPlaceResult;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.chillibits.particulatematterapi.shared.CredentialConstants;
import com.chillibits.particulatematterapi.shared.SharedUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SensorService {

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

    public List<SensorDto> getAllSensors(double latitude, double longitude, int radius, boolean onlyPublished) throws SensorDataException {
        return getSensors(latitude, longitude, radius, onlyPublished).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SensorCompressedDto> getAllSensorsCompressed(double latitude, double longitude, int radius, boolean onlyPublished) throws SensorDataException {
        return getSensors(latitude, longitude, radius, onlyPublished).stream()
                .map(this::convertToCompressedDto)
                .collect(Collectors.toList());
    }

    public SensorDto getSingleSensor(long chipId) {
        return sensorRepository.findById(chipId).map(this::convertToDto).orElse(null);
    }

    public SensorDto addSensor(SensorInsertUpdateDto sensor) throws SensorDataException {
        // Check for possible faulty data parameters
        if(sensorRepository.existsById(sensor.getChipId())) throw new SensorDataException(ErrorCodeUtils.SENSOR_ALREADY_EXISTS);
        if(!mongoTemplate.getCollectionNames().contains(String.valueOf(sensor.getChipId()))) throw new SensorDataException(ErrorCodeUtils.NO_DATA_RECORDS);
        // User can be loaded inside the validation method, cause it's needed in the addSensor and updateSensor method
        User user = validateSensorObject(sensor);

        long currentTimestamp = System.currentTimeMillis();

        // Build SensorDbo object
        Sensor sensorDbo = convertToDbo(sensor);
        retrieveCountryCityFromCoordinates(sensorDbo);
        sensorDbo.setFirmwareVersion(ConstantUtils.EMPTY_COLUMN);
        sensorDbo.setNotes(ConstantUtils.BLANK_COLUMN);
        sensorDbo.setGpsLatitude(SharedUtils.round(sensor.getGpsLatitude(), ConstantUtils.GPS_COORDINATE_ACCURACY));
        sensorDbo.setGpsLongitude(SharedUtils.round(sensor.getGpsLongitude(), ConstantUtils.GPS_COORDINATE_ACCURACY));
        sensorDbo.setCreationTimestamp(currentTimestamp);
        sensorDbo.setLastEditTimestamp(currentTimestamp);

        // Save sensor to database
        Sensor createdSensor = sensorRepository.save(sensorDbo);

        // Save UserSensorLink to the database
        Link link = new Link();
        link.setUser(user);
        link.setSensor(createdSensor);
        link.setCreationTimestamp(currentTimestamp);
        linkRepository.save(link);

        return convertToDto(createdSensor);
    }

    public Integer updateSensor(SensorInsertUpdateDto sensor) throws SensorDataException {
        // Check for possible faulty data parameters
        if(!sensorRepository.existsById(sensor.getChipId())) throw new SensorDataException(ErrorCodeUtils.SENSOR_NOT_EXISTING);
        validateSensorObject(sensor);

        Sensor sensorDbo = convertToDbo(sensor);
        retrieveCountryCityFromCoordinates(sensorDbo); // Update city and country in case that the coordinates were updated
        return sensorRepository.updateSensor(sensorDbo);
    }

    public void deleteSensorByChipId(long chipId) {
        sensorRepository.deleteById(chipId);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private List<Sensor> getSensors(double latitude, double longitude, int radius, boolean onlyPublished) throws SensorDataException {
        // Validate parameters
        if (radius < 0) throw new SensorDataException(ErrorCodeUtils.INVALID_RADIUS);

        List<Sensor> sensors;
        if (onlyPublished) {
            if (radius == 0) sensors = sensorRepository.findAllPublished();
            else sensors = sensorRepository.findAllPublishedInRadius(latitude, longitude, radius);
        } else {
            if (radius == 0) sensors = sensorRepository.findAll();
            else sensors = sensorRepository.findAllInRadius(latitude, longitude, radius);
        }

        // Set active flag, if data was received within the last few days
        sensors.forEach(sensor ->
                sensor.setActive(sensor.getLastMeasurementTimestamp() > System.currentTimeMillis() -
                        ConstantUtils.MINUTES_UNTIL_INACTIVITY * 60 * 1000)
        );

        return sensors;
    }

    private void retrieveCountryCityFromCoordinates(Sensor sensor) {
        // Retrieve country and city from latitude and longitude
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?key=" + CredentialConstants.GOOGLE_API_KEY
                    + "&latlng=" + sensor.getGpsLatitude() + "," + sensor.getGpsLongitude() + "&sensor=false&language=en";
            MapsPlaceResult place = new ObjectMapper().readValue(new URL(url), MapsPlaceResult.class);
            sensor.setCountry(place.getCountry());
            sensor.setCity(place.getCity());
        } catch (Exception e) {
            log.warn("Was not able to retrieve country and city of sensor " + sensor.getChipId());
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

    private Sensor convertToDbo(SensorInsertUpdateDto sensor) {
        return mapper.map(sensor, Sensor.class);
    }

    private User validateSensorObject(SensorInsertUpdateDto sensor) throws SensorDataException {
        // Check for invalid coordinates
        if((sensor.getGpsLatitude() == 0 && sensor.getGpsLongitude() == 0) || (sensor.getGpsLatitude() == 200 && sensor.getGpsLongitude() == 200))
            throw new SensorDataException(ErrorCodeUtils.INVALID_GPS_COORDINATES);
        // Extract requesting user
        List<LinkInsertUpdateDto> linkList = new ArrayList<>(sensor.getUserLinks());
        User user = userRepository.findById(linkList.get(0).getUser().getId()).orElse(null);
        // Check if user exists
        if(user == null) throw new SensorDataException(ErrorCodeUtils.CANNOT_ASSIGN_TO_USER);
        return user;
    }
}