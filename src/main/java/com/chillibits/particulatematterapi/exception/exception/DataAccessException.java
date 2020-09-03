/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class DataAccessException extends RuntimeException {
    // Error description list
    private static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.INVALID_MERGE_COUNT, "Invalid merge count. Must be >= 1");
        put(ErrorCode.INVALID_TIME_RANGE_DATA, "Invalid time range. Please provide an unix timestamp: from >= 0 and to >=0");
        put(ErrorCode.INVALID_FIELD_INDEX, "Invalid field index. Please provide a number >= 0. Also make sure, it's not too high.");
        put(ErrorCode.INVALID_PERIOD, "Invalid period. Please provide a period >= 1");
    }};

    public DataAccessException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}