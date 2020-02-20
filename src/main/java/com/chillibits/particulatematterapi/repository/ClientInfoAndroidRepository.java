/*
 * Copyright © 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.ClientInfoAndroid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ClientInfoAndroidRepository extends JpaRepository<ClientInfoAndroid, Integer> {
    @Modifying
    @Query("UPDATE ClientInfoAndroid c SET c.serverStatus = ?2, c.minAppVersion = ?3, c.minAppVersionName = ?4, c.latestAppVersion = ?5, c.latestAppVersionName = ?6, c.serverOwner = ?7, c.userMessage = ?8 WHERE c.id = ?1")
    Integer updateClientInfoAndroid(Integer id, Integer serverStatus, Integer minAppVersion, String minAppVersionName, Integer latestAppVersion, String latestAppVersionName, String serverOwner, String userMessage);
}