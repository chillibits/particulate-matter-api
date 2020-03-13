package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.repository.data.DataRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Api(value = "Push REST Endpoint", tags = { "push" })
public class PushController {
    DataRepository dataRepository;

    @RequestMapping(method = RequestMethod.POST, path = "/push", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Pushes a measurement record to the database")
    public String pushData() {

        return "ok";
    }
}