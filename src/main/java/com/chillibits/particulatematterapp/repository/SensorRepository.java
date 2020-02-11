/*
 * Copyright © 2019 - 2020 Marc Auberer. All rights reserved.
 */

package com.chillibits.particulatematterapp.repository;

import com.chillibits.particulatematterapp.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    @Modifying
    @Query("UPDATE Sensor s SET s.latitude = ?2, s.longitude = ?3, s.lastValueP1 = ?4, s.lastValueP2 = ?5 WHERE s.id = ?1")
    Integer updateSensor(Integer id, double latitude, double longitude, double lastValueP1, double lastValueP2);
}