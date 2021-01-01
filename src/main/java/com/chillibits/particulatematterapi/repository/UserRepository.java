/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.db.main.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.confirmationToken = ?1")
    User findByConfirmationToken(String confirmationToken);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :#{#user.firstName}, u.lastName = :#{#user.lastName}, u.password = :#{#user.password}, u.role = :#{#user.role}, u.status = :#{#user.status} WHERE u.id = :#{#user.id}")
    Integer updateUser(User user);
}