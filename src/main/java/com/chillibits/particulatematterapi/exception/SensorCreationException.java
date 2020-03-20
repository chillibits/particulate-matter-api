/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import java.util.HashMap;

public class SensorCreationException extends Exception {
    // Error description list
    private static final HashMap<Integer, String> descriptions = new HashMap<>() {{
        // Client

        // Data

        // Push

        // Ranking

        // Sensor
        put(ErrorCodes.SENSOR_ALREADY_EXISTS, "The sensor with this chip id already exists in the database.");
        put(ErrorCodes.INVALID_GPS_COORDINATES, "Invalid gps coordinates.");
        put(ErrorCodes.NO_DATA_RECORDS, "Cannot create a sensor without having received at least one data record from it.");
        put(ErrorCodes.CANNOT_ASSIGN_TO_USER, "You cannot assign a sensor to a non-existing user.");
        // Stats

        // User
        put(ErrorCodes.USER_NOT_EXISTING, "This user does not exist.");
    }};

    // Variables
    private int errorCode = 0;

    public SensorCreationException(int errorCode) {
        super("Error code " + errorCode + ": " + descriptions.get(errorCode));
        this.errorCode = errorCode;
    }
}