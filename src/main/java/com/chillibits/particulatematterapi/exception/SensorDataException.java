/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import java.util.HashMap;

public class SensorDataException extends Exception {
    // Error description list
    public static final HashMap<Integer, String> descriptions = new HashMap<>() {{
        put(ErrorCodeUtils.SENSOR_ALREADY_EXISTS, "The sensor with this chip id already exists in the database.");
        put(ErrorCodeUtils.SENSOR_NOT_EXISTING, "Cannot update a non-existing sensor.");
        put(ErrorCodeUtils.INVALID_GPS_COORDINATES, "Invalid gps coordinates.");
        put(ErrorCodeUtils.NO_DATA_RECORDS, "Cannot create a sensor without having received at least one data record from it.");
        put(ErrorCodeUtils.CANNOT_ASSIGN_TO_USER, "You cannot assign a sensor to a non-existing user.");
        put(ErrorCodeUtils.USER_NOT_EXISTING, "This user does not exist.");
        put(ErrorCodeUtils.INVALID_RADIUS, "Invalid radius. Please provide a radius >= 0");
    }};

    public SensorDataException(int errorCode) {
        // Error description as json string to process the error code on client side for localizing the error messages, presented to the users.
        super("{\"error_code\": " + errorCode + ", \"description\": \"" + descriptions.get(errorCode) + "\"}");
    }
}