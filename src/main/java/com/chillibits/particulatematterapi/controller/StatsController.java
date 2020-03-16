/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.io.Stats;
import com.chillibits.particulatematterapi.repository.main.SensorRepository;
import com.chillibits.particulatematterapi.shared.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(value = "Stats REST Endpoint", tags = { "stats" })
public class StatsController {
    private SensorRepository sensorRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about the API")
    public Stats getStats() {
        // Initialization
        long minMeasurementTimestamp = System.currentTimeMillis() - Constants.MINUTES_UNTIL_INACTIVITY;

        // Retrieve stats
        Stats stats = new Stats();
        stats.setSensorsMapTotal(sensorRepository.getSensorsMapTotal());
        stats.setSensorsMapActive(sensorRepository.getSensorsMapActive(minMeasurementTimestamp));

        // TODO: Implement stats determination

        return stats;
    }
}