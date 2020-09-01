/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.exception.RankingDataException;
import com.chillibits.particulatematterapi.model.dto.RankingItemCityCompressedDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCityDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCountryCompressedDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCountryDto;
import com.chillibits.particulatematterapi.service.RankingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(value = "Ranking REST Endpoint", tags = "ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/city", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top cities with the most sensors")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid items number. Please provide a number >= 1")
    })
    public List<RankingItemCityDto> getRankingByCity(@RequestParam(defaultValue = "10") int items) throws RankingDataException {
        return rankingService.getRankingByCity(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/city", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns a ranking of the top cities with the most sensors")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid items number. Please provide a number >= 1")
    })
    public List<RankingItemCityCompressedDto> getRankingByCityCompressed(@RequestParam(defaultValue = "10") int items) {
        return rankingService.getRankingByCityCompressed(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/country", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a ranking of the top countries with the most sensors")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid items number. Please provide a number >= 1")
    })
    public List<RankingItemCountryDto> getRankingByCountry(@RequestParam(defaultValue = "10") int items) {
        return rankingService.getRankingByCountry(items);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/ranking/country", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    @ApiOperation(value = "Returns a ranking of the top countries with the most sensors")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid items number. Please provide a number >= 1")
    })
    public List<RankingItemCountryCompressedDto> getRankingByCountryCompressed(@RequestParam(defaultValue = "10") int items) {
        return rankingService.getRankingByCountryCompressed(items);
    }
}