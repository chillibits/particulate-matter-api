/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query("UPDATE User u SET u.username = ?2, u.password = ?3, u.roles = ?4, u.active = ?5 WHERE u.id = ?1")
    Integer updateUser(Integer id, String username, String password, String roles, boolean enabled);
}