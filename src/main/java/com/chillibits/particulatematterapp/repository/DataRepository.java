/*
 * Copyright © 2019 - 2020 Marc Auberer. All rights reserved.
 */

package com.chillibits.particulatematterapp.repository;

import com.chillibits.particulatematterapp.model.DataRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<DataRecord, Integer> {

}