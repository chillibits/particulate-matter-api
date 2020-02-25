/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.DataRecord;
import com.chillibits.particulatematterapi.repository.DataRepository;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "Data REST Endpoint")
public class DataController {

    @Autowired
    DataRepository dataRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getAllDataRecords() {
        return dataRepository.findAll();
    }
}