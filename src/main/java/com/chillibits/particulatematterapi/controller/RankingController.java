/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.RankingItem;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "Ranking REST Endpoint", tags = { "ranking" })
public class RankingController {

    // Variables as objects
    @Autowired
    SensorRepository sensorRepository;
    Logger logger = LoggerFactory.getLogger(RankingController.class);

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/city", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top cities with the most sensors")
    public List<RankingItem> getRankingByCity(@RequestParam(value = "items", defaultValue = "10") int items) {
        logger.info("Ranking");
        return sensorRepository.getRankingByCity(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/country", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top countries with the most sensors")
    public List<RankingItem> getRankingByCountry(@RequestParam(value = "items", defaultValue = "10") int items) {
        return sensorRepository.getRankingByCountry(items);
    }
}