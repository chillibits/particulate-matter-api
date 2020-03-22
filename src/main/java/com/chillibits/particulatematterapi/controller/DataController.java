/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.data.DataRecord;
import com.chillibits.particulatematterapi.model.io.DataRecordDto;
import com.chillibits.particulatematterapi.shared.Constants;
import io.swagger.annotations.Api;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(value = "Data REST Endpoint", tags = "data")
public class DataController {
    @Autowired
    private MongoTemplate template;
    @Autowired
    private ModelMapper mapper;

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getDataRecords(@PathVariable long chipId, @RequestParam(defaultValue = "0") long from, @RequestParam(defaultValue = "0") long to) {
        if(to == 0) to = System.currentTimeMillis();
        if(from == 0) from = to - Constants.DEFAULT_DATA_TIMESPAN;
        return template.find(Query.query(Criteria.where("timestamp").gte(from).lte(to)), DataRecord.class, String.valueOf(chipId));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/data/{chipId}", produces = MediaType.APPLICATION_JSON_VALUE, params = "compressed")
    public List<Object> getDataRecordsCompressed(
            @PathVariable long chipId,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to,
            @RequestParam(defaultValue = "false") boolean compressed
    ) {
        if(to == 0) to = System.currentTimeMillis();
        if(from == 0) from = to - Constants.DEFAULT_DATA_TIMESPAN;

        List<DataRecord> records = template.find(Query.query(Criteria.where("timestamp").gte(from).lte(to)), DataRecord.class, String.valueOf(chipId));
        if(compressed) {
            return records.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(records);
    }

    private DataRecordDto convertToDto(DataRecord record) {
        DataRecordDto dataRecordDto = mapper.map(record, DataRecordDto.class);
        dataRecordDto.setTimestamp(dataRecordDto.getTimestamp() / 1000);
        return dataRecordDto;
    }

    private DataRecord convertToEntity(DataRecordDto recordDto) {
        DataRecord record = mapper.map(recordDto, DataRecord.class);
        record.setTimestamp(record.getTimestamp() * 1000);
        return record;
    }
}