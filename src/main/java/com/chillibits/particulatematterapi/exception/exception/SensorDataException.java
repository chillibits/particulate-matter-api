/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception.exception;

import com.chillibits.particulatematterapi.exception.ErrorCode;

import java.util.HashMap;

public class SensorDataException extends RuntimeException {
    // Error description list
    public static final HashMap<ErrorCode, String> descriptions = new HashMap<>() {{
        put(ErrorCode.SENSOR_ALREADY_EXISTS, "The sensor with this chip id already exists in the database.");
        put(ErrorCode.SENSOR_NOT_EXISTING, "Cannot update a non-existing sensor.");
        put(ErrorCode.INVALID_GPS_COORDINATES, "Invalid gps coordinates.");
        put(ErrorCode.NO_DATA_RECORDS, "Cannot create a sensor without having received at least one data record from it.");
        put(ErrorCode.CANNOT_ASSIGN_TO_USER, "You cannot assign a sensor to a non-existing user.");
        put(ErrorCode.USER_NOT_EXISTING, "This user does not exist.");
        put(ErrorCode.INVALID_RADIUS, "Invalid radius. Please provide a radius >= 0");
    }};

    public SensorDataException(ErrorCode errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}