/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.RankingDataException;
import com.chillibits.particulatematterapi.model.dto.RankingItemCityDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCountryDto;
import com.chillibits.particulatematterapi.model.io.RankingItemCity;
import com.chillibits.particulatematterapi.model.io.RankingItemCountry;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Ranking Controller")
public class RankingControllerTests {

    @Autowired
    private RankingController rankingController;
    @MockBean
    private SensorRepository sensorRepository;

    private final List<RankingItemCity> testDataCity = getTestDataCity();
    private final List<RankingItemCountry> testDataCountry = getTestDataCountry();
    private final List<RankingItemCityDto> assertDataCity = getAssertDataCity();
    private final List<RankingItemCountryDto> assertDataCountry = getAssertDataCountry();

    @TestConfiguration
    static class SensorControllerImplTestContextConfiguration {

        @Bean
        public RankingController rankingController() {
            return new RankingController();
        }

        @Bean
        public ModelMapper modelMapper() {
            return new ModelMapper();
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        Mockito.when(sensorRepository.getRankingByCity(anyInt())).thenReturn(testDataCity);
        Mockito.when(sensorRepository.getRankingByCountry(anyInt())).thenReturn(testDataCountry);
    }

    // ----------------------------------------------- Get city ranking ------------------------------------------------

    @Test
    public void getCityRankingSuccessfully() throws RankingDataException {
        List<RankingItemCity> result = rankingController.getRankingByCity(5);
        assertThat(result).containsExactlyInAnyOrder(testDataCity.toArray(RankingItemCity[]::new));
    }

    @Test
    public void getCityRankingInvalidItemsNumber() {
        // Try with invalid input
        Exception exception = assertThrows(RankingDataException.class, () ->
                rankingController.getRankingByCity(-1)
        );

        String expectedMessage = new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getCityRankingCompressedSuccessfully() throws RankingDataException {
        List<RankingItemCityDto> result = rankingController.getRankingByCityCompressed(5);
        assertThat(result).containsExactlyInAnyOrder(assertDataCity.toArray(RankingItemCityDto[]::new));
    }

    @Test
    public void getCityRankingCompressedInvalidItemsNumber() {
        // Try with invalid input
        Exception exception = assertThrows(RankingDataException.class, () ->
                rankingController.getRankingByCityCompressed(-1)
        );

        String expectedMessage = new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // --------------------------------------------- Get country ranking -----------------------------------------------

    @Test
    public void getCountryRankingSuccessfully() throws RankingDataException {
        List<RankingItemCountry> result = rankingController.getRankingByCountry(5);
        assertThat(result).containsExactlyInAnyOrder(testDataCountry.toArray(RankingItemCountry[]::new));
    }

    @Test
    public void getCountryRankingInvalidItemsNumber() {
        // Try with invalid input
        Exception exception = assertThrows(RankingDataException.class, () ->
                rankingController.getRankingByCountry(-1)
        );

        String expectedMessage = new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void getCountryRankingCompressedSuccessfully() throws RankingDataException {
        List<RankingItemCountryDto> result = rankingController.getRankingByCountryCompressed(5);
        assertThat(result).containsExactlyInAnyOrder(assertDataCountry.toArray(RankingItemCountryDto[]::new));
    }

    @Test
    public void getCountryRankingCompressedInvalidItemsNumber() {
        // Try with invalid input
        Exception exception = assertThrows(RankingDataException.class, () ->
                rankingController.getRankingByCountryCompressed(-1)
        );

        String expectedMessage = new RankingDataException(ErrorCodeUtils.INVALID_ITEMS_NUMBER).getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    private List<RankingItemCity> getTestDataCity() {
        // Create ranking item objects
        RankingItemCity r1 = new RankingItemCity("Russia", "Moskva", 55);
        RankingItemCity r2 = new RankingItemCity("Germany", "Berlin", 42);
        RankingItemCity r3 = new RankingItemCity("Germany", "Hamburg", 22);
        RankingItemCity r4 = new RankingItemCity("Germany", "München", 22);
        RankingItemCity r5 = new RankingItemCity("Italy", "Parma", 22);

        // Add them to test data
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private List<RankingItemCountry> getTestDataCountry() {
        // Create ranking item objects
        RankingItemCountry r1 = new RankingItemCountry("Germany", 722);
        RankingItemCountry r2 = new RankingItemCountry("Poland", 203);
        RankingItemCountry r3 = new RankingItemCountry("Italy", 149);
        RankingItemCountry r4 = new RankingItemCountry("Russia", 124);
        RankingItemCountry r5 = new RankingItemCountry("Netherlands", 84);

        // Add them to test data
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private List<RankingItemCityDto> getAssertDataCity() {
        // Create ranking item objects
        RankingItemCityDto r1 = new RankingItemCityDto("Russia", "Moskva", 55);
        RankingItemCityDto r2 = new RankingItemCityDto("Germany", "Berlin", 42);
        RankingItemCityDto r3 = new RankingItemCityDto("Germany", "Hamburg", 22);
        RankingItemCityDto r4 = new RankingItemCityDto("Germany", "München", 22);
        RankingItemCityDto r5 = new RankingItemCityDto("Italy", "Parma", 22);

        // Add them to test data
        return Arrays.asList(r1, r2, r3, r4, r5);
    }

    private List<RankingItemCountryDto> getAssertDataCountry() {
        // Create ranking item objects
        RankingItemCountryDto r1 = new RankingItemCountryDto("Germany", 722);
        RankingItemCountryDto r2 = new RankingItemCountryDto("Poland", 203);
        RankingItemCountryDto r3 = new RankingItemCountryDto("Italy", 149);
        RankingItemCountryDto r4 = new RankingItemCountryDto("Russia", 124);
        RankingItemCountryDto r5 = new RankingItemCountryDto("Netherlands", 84);

        // Add them to test data
        return Arrays.asList(r1, r2, r3, r4, r5);
    }
}