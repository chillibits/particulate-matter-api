/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.db.main.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByName(String name);

    /*@Modifying
    @Query("UPDATE Client c SET c.name = ?2, c.readableName = ?3, c.secret = ?4, c.type = ?5, c.roles = ?6, c.status = ?7, c.active = ?8, c.minVersion = ?9, c.minVersionName = ?10, c.latestVersion = ?11, c.latestVersionName = ?12, c.owner = ?13, c.userMessage = ?14 WHERE c.id = ?1")
    Integer updateClient(int id, String name, String readableName, String secret, int type, String roles, int status, boolean active, int minVersion, String minVersionName, int latestVersion, String latestVersionName, String owner, String userMessage);*/

    @Modifying
    @Query("UPDATE Client c SET c.name = :#{#client.name}, c.readableName = :#{#client.readableName}, c.secret = :#{#client.secret}, c.type = :#{#client.type}, c.roles = :#{#client.roles}, c.status = :#{#client.status}, c.active = :#{#client.active}, c.minVersion = :#{#client.minVersion}, c.minVersionName = :#{#client.minVersionName}, c.latestVersion = :#{#client.latestVersion}, c.latestVersionName = :#{#client.latestVersionName}, c.owner = :#{#client.owner}, c.userMessage = :#{#client.userMessage} WHERE c.id = ?1")
    Integer updateClient(Client client);
}