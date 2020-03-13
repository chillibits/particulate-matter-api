package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.DataRecord;
import com.chillibits.particulatematterapi.repository.data.DataRepository;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Data REST Endpoint", tags = { "data" })
public class DataController {
    DataRepository dataRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DataRecord> getAllDataRecords() {
        return dataRepository.findAll();
    }
}