/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class LinkDataException extends RuntimeException {
    // Error description list
    private static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.USER_NOT_EXISTING, "Cannot assign link to a non-existent user.");
        put(ErrorCode.SENSOR_NOT_EXISTING, "Cannot assign link to a non-existent sensor.");
        put(ErrorCode.INVALID_LINK_DATA, "Invalid link data.");
    }};

    public LinkDataException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}