/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.repository;

import com.mrgames13.jimdo.particulatematterapp.model.DataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DataRepository extends JpaRepository<DataRecord, Integer> {
    @Modifying
    @Query("")
    Integer updateDataRecord();
}