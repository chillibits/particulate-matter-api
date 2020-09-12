/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class UserDataException extends RuntimeException {
    // Error description list
    private static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.INVALID_USER_DATA, "Please provide an user object with all fields filled");
        put(ErrorCode.USER_ALREADY_EXISTS, "This user already exists");
        put(ErrorCode.USER_NOT_EXISTING, "This user does not exist");
        put(ErrorCode.PASSWORD_WRONG, "The user exists, but the provided password is wrong");
    }};

    public UserDataException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode.getCode() + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}