/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.RankingDataException;
import com.chillibits.particulatematterapi.model.io.RankingItem;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Ranking REST Endpoint", tags = "ranking")
public class RankingController {
    private SensorRepository sensorRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/city", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top cities with the most sensors")
    public List<RankingItem> getRankingByCity(
            @RequestParam(defaultValue = "10") int items
    ) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCity(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/country", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top countries with the most sensors")
    public List<RankingItem> getRankingByCountry(
            @RequestParam(defaultValue = "10") int items
    ) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCountry(items);
    }
}