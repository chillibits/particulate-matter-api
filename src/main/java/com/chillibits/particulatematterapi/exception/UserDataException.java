/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import java.util.HashMap;

public class UserDataException extends Exception {
    // Error description list
    private static final HashMap<Integer, String> descriptions = new HashMap<>() {{
        put(ErrorCodeUtils.INVALID_USER_DATA, "Please provide an user object with all fields filled");
        put(ErrorCodeUtils.USER_ALREADY_EXISTS, "This user already exists");
    }};

    public UserDataException(int errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}