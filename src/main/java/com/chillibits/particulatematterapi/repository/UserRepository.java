/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.db.main.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Modifying
    @Query("UPDATE User u SET u.firstName = ?2, u.lastName = ?3, u.password = ?4, u.role = ?5, u.status = ?6 WHERE u.id = ?1")
    Integer updateUser(int id, String firstName, String lastName, String password, int role, int status);
}