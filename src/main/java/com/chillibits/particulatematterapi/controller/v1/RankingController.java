/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.RankingDataException;
import com.chillibits.particulatematterapi.model.io.RankingItemCity;
import com.chillibits.particulatematterapi.model.io.RankingItemCityDto;
import com.chillibits.particulatematterapi.model.io.RankingItemCountry;
import com.chillibits.particulatematterapi.model.io.RankingItemCountryDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Api(value = "Ranking REST Endpoint", tags = "ranking")
public class RankingController {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private ModelMapper mapper;

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/city", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top cities with the most sensors")
    public List<RankingItemCity> getRankingByCity(
            @RequestParam(defaultValue = "10") int items
    ) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCity(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/city", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns a ranking of the top cities with the most sensors")
    public List<RankingItemCityDto> getRankingByCityCompressed(
            @RequestParam(defaultValue = "10") int items
    ) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCity(items)
                .stream()
                .map(this::convertToCityDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/country", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top countries with the most sensors")
    public List<RankingItemCountry> getRankingByCountry(
            @RequestParam(defaultValue = "10") int items
    ) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCountry(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/country", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    public List<RankingItemCountryDto> getRankingByCountryCompressed(
            @RequestParam(defaultValue = "10") int items
    ) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCountry(items)
                .stream()
                .map(this::convertToCountryDto)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private RankingItemCityDto convertToCityDto(RankingItemCity item) {
        return mapper.map(item, RankingItemCityDto.class);
    }

    private RankingItemCountryDto convertToCountryDto(RankingItemCountry item) {
        return mapper.map(item, RankingItemCountryDto.class);
    }
}