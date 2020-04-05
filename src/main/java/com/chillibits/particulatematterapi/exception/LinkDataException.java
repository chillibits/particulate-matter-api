/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import java.util.HashMap;

public class LinkDataException extends Exception {
    // Error description list
    private static final HashMap<Integer, String> descriptions = new HashMap<>() {{
        put(ErrorCodeUtils.USER_NOT_EXISTING, "Cannot assign link to a non-existent user.");
        put(ErrorCodeUtils.SENSOR_NOT_EXISTING, "Cannot assign link to a non-existent sensor.");
    }};

    public LinkDataException(int errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}