/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    @Modifying
    @Query("UPDATE Sensor s SET s.latitude = ?2, s.longitude = ?3, s.lastValueP1 = ?4, s.lastValueP2 = ?5 WHERE s.id = ?1")
    Integer updateSensor(Integer id, double latitude, double longitude, double lastValueP1, double lastValueP2);
}