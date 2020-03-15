/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.shared.Constants;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Data REST Endpoint", tags = { "data" })
public class DataController {
    MongoTemplate template;

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getDataRecords(@PathVariable long chipId, @RequestParam(defaultValue = "0") long from, @RequestParam(defaultValue = "0") long to) {
        if(to == 0) to = System.currentTimeMillis();
        if(from == 0) from = to - Constants.DEFAULT_DATA_TIMESPAN;
        return template.find(Query.query(Criteria.where("timestamp").gte(from).lte(to)), DataRecord.class, String.valueOf(chipId));
    }
}