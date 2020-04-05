/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

public class ErrorCodeUtils {
    // Client errors (1xx)
    public static final int INVALID_CLIENT_DATA = 100;

    // Data errors (2xx)
    public static final int INVALID_TIME_RANGE_DATA = 200;
    public static final int INVALID_FIELD_INDEX = 201;
    public static final int INVALID_MERGE_COUNT = 202;
    public static final int INVALID_PERIOD = 203;

    // Push errors (3xx)

    // Ranking errors (4xx)
    public static final int INVALID_ITEMS_NUMBER = 400;

    // Sensor errors (5xx)
    public static final int SENSOR_ALREADY_EXISTS = 500;
    public static final int SENSOR_NOT_EXISTING = 501;
    public static final int INVALID_GPS_COORDINATES = 502;
    public static final int NO_DATA_RECORDS = 503;
    public static final int CANNOT_ASSIGN_TO_USER = 504;
    public static final int INVALID_RADIUS = 505;

    // Stats errors (6xx)

    // User errors (7xx)
    public static final int INVALID_USER_DATA = 700;
    public static final int USER_NOT_EXISTING = 701;

    // Link errors (8xx)
    public static final int INVALID_LINK_DATA = 800;

    // Log errors (9xx)
    public static final int INVALID_TIME_RANGE_LOG = 900;
}