/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import com.chillibits.particulatematterapi.exception.exception.ClientDataException;
import com.chillibits.particulatematterapi.exception.exception.DataAccessException;
import com.chillibits.particulatematterapi.exception.exception.LinkDataException;
import com.chillibits.particulatematterapi.exception.exception.LogAccessException;
import com.chillibits.particulatematterapi.exception.exception.RankingDataException;
import com.chillibits.particulatematterapi.exception.exception.SensorDataException;
import com.chillibits.particulatematterapi.exception.exception.StatsDataException;
import com.chillibits.particulatematterapi.exception.exception.UserDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class ExceptionAdvices {

    /**
     * This method handles all known exceptions
     *
     * @param e the thrown exception
     * @return the response message from each individual exception
     */
    @ResponseBody
    @ExceptionHandler({
            ClientDataException.class,
            DataAccessException.class,
            LinkDataException.class,
            LogAccessException.class,
            RankingDataException.class,
            SensorDataException.class,
            SensorDataException.class,
            StatsDataException.class,
            UserDataException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handler(RuntimeException e) { return e.getMessage(); }

    /**
     * This method handles all unknown exceptions and server errors
     *
     * @param e the thrown exception
     * @param request the WebRequest
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public void unknownException(Exception e, WebRequest request) {
        log.error("Unable to handle {}", request.getDescription(false), e);
    }
}
