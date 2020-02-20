/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.DataRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataRepository extends JpaRepository<DataRecord, Integer> {

}