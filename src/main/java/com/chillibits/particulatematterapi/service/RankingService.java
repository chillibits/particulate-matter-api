/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.RankingDataException;
import com.chillibits.particulatematterapi.model.dto.RankingItemCityCompressedDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCityDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCountryCompressedDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCountryDto;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private ModelMapper mapper;

    public List<RankingItemCityDto> getRankingByCity(Integer items) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCity(items);
    }

    public List<RankingItemCityCompressedDto> getRankingByCityCompressed(Integer items) throws RankingDataException {
        return getRankingByCity(items).stream()
                .map(this::convertToCityDto)
                .collect(Collectors.toList());
    }

    public List<RankingItemCountryDto> getRankingByCountry(Integer items) throws RankingDataException {
        if(items < 1) throw new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER);
        return sensorRepository.getRankingByCountry(items);
    }

    public List<RankingItemCountryCompressedDto> getRankingByCountryCompressed(Integer items) throws RankingDataException {
        return getRankingByCountry(items).stream()
                .map(this::convertToCountryDto)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private RankingItemCityCompressedDto convertToCityDto(RankingItemCityDto item) {
        return mapper.map(item, RankingItemCityCompressedDto.class);
    }

    private RankingItemCountryCompressedDto convertToCountryDto(RankingItemCountryDto item) {
        return mapper.map(item, RankingItemCountryCompressedDto.class);
    }
}