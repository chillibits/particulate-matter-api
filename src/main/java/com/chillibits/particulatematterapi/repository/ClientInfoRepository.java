/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.ClientInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ClientInfoRepository extends JpaRepository<ClientInfo, Integer> {
    @Modifying
    @Query("UPDATE ClientInfo c SET c.serverStatus = ?2, c.minVersion = ?3, c.minVersionName = ?4, c.latestVersion = ?5, c.latestVersionName = ?6, c.serverOwner = ?7, c.userMessage = ?8 WHERE c.id = ?1")
    Integer updateClientInfo(Integer id, Integer serverStatus, Integer minAppVersion, String minAppVersionName, Integer latestAppVersion, String latestAppVersionName, String serverOwner, String userMessage);
}