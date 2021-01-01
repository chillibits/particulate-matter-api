/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class ClientDataException extends RuntimeException {
    // Error description list
    private static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.CLIENT_NOT_EXISTING, "This client does not exist");
        put(ErrorCode.INVALID_CLIENT_DATA, "Please provide a client object with all fields filled");
    }};

    public ClientDataException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode.getCode() + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}