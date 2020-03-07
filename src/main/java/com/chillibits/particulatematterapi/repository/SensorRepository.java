/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.RankingItem;
import com.chillibits.particulatematterapi.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    @Modifying
    @Query("UPDATE Sensor s SET s.gpsLatitude = ?2, s.gpsLongitude = ?3, s.lastValueP1 = ?4, s.lastValueP2 = ?5 WHERE s.chipId = ?1")
    Integer updateSensor(int chipId, double latitude, double longitude, double lastValueP1, double lastValueP2);

    @Query(value = "SELECT new com.chillibits.particulatematterapi.model.RankingItem(s.country, s.city, COUNT(s.city)) FROM Sensor s GROUP BY s.city, s.country ORDER BY COUNT(s.city)")
    List<RankingItem> getRankingByCity(int items);

    @Query("SELECT new com.chillibits.particulatematterapi.model.RankingItem(s.country, '', COUNT(s.country)) FROM Sensor s GROUP BY s.country ORDER BY COUNT(s.country)")
    List<RankingItem> getRankingByCountry(int items);
}