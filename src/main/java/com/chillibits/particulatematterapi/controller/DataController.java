package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.DataRecord;
import com.chillibits.particulatematterapi.repository.data.DataRepository;
import com.chillibits.particulatematterapi.shared.Constants;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Data REST Endpoint", tags = { "data" })
public class DataController {
    DataRepository dataRepository;
    MongoOperations operations;

    @RequestMapping(method = RequestMethod.GET, path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getAllDataRecords() {
        return dataRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getDataRecords(@PathVariable long chipId, @RequestParam(defaultValue = "0") long from, @RequestParam(defaultValue = "0") long to) {
        if(to == 0) to = System.currentTimeMillis();
        if(from == 0) from = to - Constants.DEFAULT_DATA_TIMESPAN;
        return operations.findDistinct(Query.query(Criteria.where("timestamp").gte(from).and("timestamp").lte(to)), "timestamp", String.valueOf(chipId), DataRecord.class);
    }
}