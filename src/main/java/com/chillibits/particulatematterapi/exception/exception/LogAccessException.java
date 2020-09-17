/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class LogAccessException extends RuntimeException {
    // Error description list
    private static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.INVALID_TIME_RANGE_LOG, "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0");
    }};

    public LogAccessException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode.getCode() + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}