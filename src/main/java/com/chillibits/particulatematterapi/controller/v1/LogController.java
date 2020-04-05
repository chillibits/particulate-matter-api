/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.LogAccessException;
import com.chillibits.particulatematterapi.model.db.data.LogItem;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "Sensor REST Endpoint", tags = "sensor", hidden = true)
public class LogController {

    @Autowired
    private MongoTemplate template;

    @RequestMapping(method = RequestMethod.GET, path = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span")
    public List<LogItem> getAllLogs(
            @RequestParam long from,
            @RequestParam long to
    ) throws LogAccessException {
        if(from < 0 || to < 0) throw new LogAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_LOG);
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        return template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp)), LogItem.class);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/log/target/{target}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by target")
    public List<LogItem> getLogsTarget(
            @PathVariable String target,
            @RequestParam long from,
            @RequestParam long to
    ) throws LogAccessException {
        if(from < 0 || to < 0) throw new LogAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_LOG);
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        return template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp).and("target").regex(target)), LogItem.class);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/log/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by user")
    public List<LogItem> getLogsUser(
            @PathVariable int userId,
            @RequestParam long from,
            @RequestParam long to
    ) throws LogAccessException {
        if(from < 0 || to < 0) throw new LogAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_LOG);
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        return template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp).and("userId").is(userId)), LogItem.class);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/log/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by client")
    public List<LogItem> getLogsClient(
            @PathVariable int clientId,
            @RequestParam long from,
            @RequestParam long to
    ) throws LogAccessException {
        if(from < 0 || to < 0) throw new LogAccessException(ErrorCodeUtils.INVALID_TIME_RANGE_LOG);
        long toTimestamp = to == 0 ? System.currentTimeMillis() : to;
        long fromTimestamp = from == 0 ? toTimestamp - ConstantUtils.DEFAULT_DATA_TIME_SPAN : from;

        return template.find(Query.query(Criteria.where("timestamp").gte(fromTimestamp).lte(toTimestamp).and("clientId").is(clientId)), LogItem.class);
    }
}