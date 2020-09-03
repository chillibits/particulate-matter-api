/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.dto.StatsItemDto;
import com.chillibits.particulatematterapi.service.StatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(value = "Stats REST Endpoint", tags = "stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @RequestMapping(method = RequestMethod.GET, path = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about the API")
    public StatsItemDto getStats() {
        return statsService.getAllStats();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stats/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns stats about a specific sensor")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "The sensor you're requesting does not exist.")
    })
    public StatsItemDto getStatsOfSensor(@PathVariable long chipId) {
        return statsService.getStatsBySensor(chipId);
    }
}