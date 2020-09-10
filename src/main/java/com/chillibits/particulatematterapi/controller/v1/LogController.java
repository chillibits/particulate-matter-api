/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.dto.LogItemDto;
import com.chillibits.particulatematterapi.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Log endpoint
 *
 * Endpoint to retrieve log information.
 * Due to data protection, this endpoint only can be used by applications with the AA role (admin application)
 */
@RestController
@Api(value = "Log REST Endpoint", tags = "log")
@ApiIgnore
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * Returns the logs for the specified time span
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @param from Begin of the requested time range (unit timestamp in milliseconds)
     * @param to End of the requested time range (unit timestamp in milliseconds)
     * @return List of log items as List of LogItemDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/log", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<LogItemDto> getAllLogs(@RequestParam(defaultValue = "0") long from, @RequestParam(defaultValue = "0") long to) {
        return logService.getAllLogs(from, to);
    }

    /**
     * Returns the logs for the specified time span, filtered by target
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @param target String, which identifies a process target (e.g. l1 for the link with id 1)
     * @param from Begin of the requested time range (unit timestamp in milliseconds)
     * @param to End of the requested time range (unit timestamp in milliseconds)
     * @return List of log items as List of LogItemDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/log/target/{target}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by target", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<LogItemDto> getLogsByTarget(
            @PathVariable String target,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to
    ) {
        return logService.getLogsByTarget(target, from, to);
    }

    /**
     * Returns the logs for the specified time span, filtered by user
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @param userId Id of the executing user
     * @param from Begin of the requested time range (unit timestamp in milliseconds)
     * @param to End of the requested time range (unit timestamp in milliseconds)
     * @return List of log items as List of LogItemDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/log/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by user", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<LogItemDto> getLogsByUser(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to
    ) {
        return logService.getLogsByUser(userId, from, to);
    }

    /**
     * Returns the logs for the specified time span, filtered by client
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @param clientId Id of the executing client
     * @param from Begin of the requested time range (unit timestamp in milliseconds)
     * @param to End of the requested time range (unit timestamp in milliseconds)
     * @return List of log items as List of LogItemDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/log/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by client", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<LogItemDto> getLogsByClient(
            @PathVariable int clientId,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to
    ) {
        return logService.getLogsByClient(clientId, from, to);
    }

    /**
     * Returns the logs for the specified time span, filtered by action
     * <p>Note: Requires application role AA (admin application)</p>
     *
     * @param action Name of the executed action (e.g. update-user)
     * @param from Begin of the requested time range (unit timestamp in milliseconds)
     * @param to End of the requested time range (unit timestamp in milliseconds)
     * @return List of log items as List of LogItemDto
     */
    @RequestMapping(method = RequestMethod.GET, path = "/log/action/{action}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the logs for the specified time span, filtered by action", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0")
    })
    public List<LogItemDto> getLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") long from,
            @RequestParam(defaultValue = "0") long to
    ) {
        return logService.getLogsByAction(action, from, to);
    }
}