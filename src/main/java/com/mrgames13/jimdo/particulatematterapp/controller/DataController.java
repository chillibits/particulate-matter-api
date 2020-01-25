/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.controller;

import com.mrgames13.jimdo.particulatematterapp.model.DataRecord;
import com.mrgames13.jimdo.particulatematterapp.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class DataController {

    @Autowired
    DataRepository dataRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getAllDataRecords() {
        return dataRepository.findAll();
    }
}