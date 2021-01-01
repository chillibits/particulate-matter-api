/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.dto.RankingItemCityDto;
import com.chillibits.particulatematterapi.model.dto.RankingItemCountryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.Meta;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    @Meta(cursorBatchSize = 100)
    @Query(value = "SELECT *, (6371000 * acos(cos(radians(?1)) * cos(radians(s.gps_latitude)) * cos(radians(s.gps_longitude) - radians(?2)) + sin(radians(?1)) * sin(radians(s.gps_latitude)))) AS distance FROM sensor s WHERE distance <= ?3 GROUP BY distance ORDER BY distance", nativeQuery = true)
    List<Sensor> findAllInRadius(double lat, double lng, int radius);

    @Meta(cursorBatchSize = 100)
    @Query("SELECT s FROM Sensor s WHERE s.published = 1")
    List<Sensor> findAllPublished();

    @Meta(cursorBatchSize = 100)
    @Query(value = "SELECT *, (6371000 * acos(cos(radians(?1)) * cos(radians(s.gps_latitude)) * cos(radians(s.gps_longitude) - radians(?2)) + sin(radians(?1)) * sin(radians(s.gps_latitude)))) AS distance FROM sensor s WHERE distance <= ?3 AND s.published = 1 GROUP BY distance ORDER BY distance", nativeQuery = true)
    List<Sensor> findAllPublishedInRadius(double lat, double lng, int radius);

    @Meta(cursorBatchSize = 100)
    @Query(value = "SELECT new com.chillibits.particulatematterapi.model.dto.RankingItemCityDto(s.country, s.city, COUNT(s.city)) FROM Sensor s GROUP BY s.city, s.country ORDER BY COUNT(s.city) DESC, s.country, s.city")
    List<RankingItemCityDto> getRankingByCity(int items);

    @Meta(cursorBatchSize = 10)
    @Query("SELECT new com.chillibits.particulatematterapi.model.dto.RankingItemCountryDto(s.country, COUNT(s.country)) FROM Sensor s GROUP BY s.country ORDER BY COUNT(s.country) DESC, s.country")
    List<RankingItemCountryDto> getRankingByCountry(int items);

    @Meta(cursorBatchSize = 10)
    @Query("SELECT s.chipId FROM Sensor s WHERE s.country = ?1")
    List<Long> getChipIdsOfSensorFromCountry(String country);

    @Meta(cursorBatchSize = 100)
    @Query("SELECT s.chipId FROM Sensor s WHERE s.country = ?1 AND s.city = ?2")
    List<Long> getChipIdsOfSensorFromCity(String country, String city);

    @Meta(cursorBatchSize = 100)
    @Query("SELECT s FROM Sensor s WHERE s.chipId IN ?1")
    List<Sensor> getSensorsFromIdRange(List<Integer> chipIds);

    @Meta(cursorBatchSize = 100)
    @Query("SELECT COUNT(s.chipId) FROM Sensor s")
    Integer getSensorsMapTotal();

    @Meta(cursorBatchSize = 100)
    @Query("SELECT COUNT(s.chipId) FROM Sensor s WHERE s.lastMeasurementTimestamp > ?1")
    Integer getSensorsMapActive(long minLastMeasurementTimestamp);

    @Modifying
    @Transactional
    @Query("UPDATE Sensor s SET s.gpsLatitude = :#{#sensor.gpsLatitude}, s.gpsLongitude = :#{#sensor.gpsLongitude}, s.country = :#{#sensor.country}, s.city = :#{#sensor.city}, s.lastEditTimestamp = :#{#sensor.lastEditTimestamp}, s.notes = :#{#sensor.notes}, s.indoor = :#{#sensor.indoor}, s.published = :#{#sensor.published} WHERE s.chipId = :#{#sensor.chipId}")
    Integer updateSensor(Sensor sensor);
}