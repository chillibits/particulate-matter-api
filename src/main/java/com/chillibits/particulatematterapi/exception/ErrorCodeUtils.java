/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

public class ErrorCodeUtils {
    // Client errors (1xx)

    // Data errors (2xx)

    // Push errors (3xx)

    // Ranking errors (4xx)

    // Sensor errors (5xx)
    public static final int SENSOR_ALREADY_EXISTS = 500;
    public static final int INVALID_GPS_COORDINATES = 501;
    public static final int NO_DATA_RECORDS = 502;
    public static final int CANNOT_ASSIGN_TO_USER = 503;
    // Stats errors (6xx)

    // User errors (7xx)
    public static final int USER_NOT_EXISTING = 700;
}