/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class StatsDataException extends RuntimeException {
    // Error description list
    private static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.STATS_ITEM_DOES_NOT_EXIST, "The sensor you're requesting does not exist.");
    }};

    public StatsDataException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode.getCode() + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}