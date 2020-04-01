/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.db.main.UserSensorLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSensorLinkRepository extends JpaRepository<UserSensorLink, Integer> {}
