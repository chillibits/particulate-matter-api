/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Client errors (1xx)
    CLIENT_NOT_EXISTING(100),
    INVALID_CLIENT_DATA(101),

    // Data errors (2xx)
    INVALID_TIME_RANGE_DATA(200),
    INVALID_FIELD_INDEX(201),
    INVALID_MERGE_COUNT(202),
    INVALID_PERIOD(203),

    // Push errors (3xx)
    NO_DATA_VALUES(300),

    // Ranking errors (4xx)
    INVALID_ITEMS_NUMBER(400),

    // Sensor errors (5xx)
    SENSOR_ALREADY_EXISTS(500),
    SENSOR_NOT_EXISTING(501),
    INVALID_GPS_COORDINATES(502),
    NO_DATA_RECORDS(503),
    CANNOT_ASSIGN_TO_USER(504),
    INVALID_RADIUS(505),

    // Stats errors (6xx)
    STATS_ITEM_DOES_NOT_EXIST(600),

    // User errors (7xx)
    INVALID_USER_DATA(700),
    USER_NOT_EXISTING(701),
    USER_ALREADY_EXISTS(702),
    PASSWORD_WRONG(703),

    // Link errors (8xx)
    INVALID_LINK_DATA(800),

    // Log errors (9xx)
    INVALID_TIME_RANGE_LOG(900);

    private final int value;
}