/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.model.dto.DataRecordInsertUpdateDto;
import com.chillibits.particulatematterapi.service.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Push endpoint
 *
 * Endpoint for receiving measurement data from sensors.
 * <p>Note: Please use an unsecured http connection to send data to this endpoint. This saves server capacity</p>
 */
@RestController
@Api(value = "Push REST Endpoint", tags = "push")
public class PushController {

    @Autowired
    private PushService pushService;

    /**
     * Pushes a measurement record to the database
     * <p>Note: Please use an unsecured http connection to send data to this endpoint. This saves server capacity</p>
     *
     * @param record Instance of DataRecordInsertUpdateDto with all required data values
     * @param xSensorHeader Header attribute which contains the Chip-Id of a sensor with a pre-/suffix (e.g. esp8266-4017638)
     * @param sensorHeader Header attribute which contains the Chip-Id of a sensor with a pre-/suffix (e.g. esp8266-4017638)
     * @return "ok" / "error"
     */
    @RequestMapping(method = RequestMethod.POST, path = "/push", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Pushes a measurement record to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "This record does not contain any data values")
    })
    public String pushData(@RequestBody DataRecordInsertUpdateDto record, @RequestHeader(value = "X-Sensor", defaultValue = "0") String xSensorHeader, @RequestHeader(value = "Sensor", defaultValue = "0") String sensorHeader) {
        // Set chip id value correctly
        if(record.getChipId() == 0 && xSensorHeader.contains("-")) record.setChipId(Long.parseLong(xSensorHeader.substring(xSensorHeader.indexOf("-") +1)));
        if(record.getChipId() == 0 && sensorHeader.contains("-")) record.setChipId(Long.parseLong(sensorHeader.substring(xSensorHeader.indexOf("-") +1)));
        // Push record into the database
        return pushService.pushData(record) ? "ok" : "error";
    }
}