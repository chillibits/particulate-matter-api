/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.shared;

public class ConstantUtils {
    public static final String EMPTY_COLUMN = "-";
    public static final String BLANK_COLUMN = "";
    public static final int GPS_COORDINATE_ACCURACY = 4; // Number of decimal places
    public static final long DEFAULT_DATA_TIME_SPAN = 24 * 60 * 60 * 1000; // One day in milliseconds
    public static final int MINUTES_UNTIL_INACTIVITY = 4320; // 3 days in minutes
    public static final int UNKNOWN_USER_ID = 1;
    public static final int UNKNOWN_CLIENT_ID = 1;
    public static final String LOG_TABLE_NAME = "0_AccessLog";
    public static final String STATS_TABLE_NAME = "0_StatsStore";
    public static final boolean IMPORT_SENSORS_IF_TABLE_IS_EMPTY = true;
    public static final boolean INDEX_DB_ON_STARTUP = false;
    public static final boolean CALC_STATS_ON_STARTUP = false;
    public static final long ROLLBACK_TIMESTAMP = 0; // Set to 0 to disable rollback
    public static final int CLIENT_ID_PMAPP = 1; // Official Particulate Matter App
    public static final int CLIENT_ID_PMAPP_WEB = 2; // Official Particulate Matter App Web
    public static final int CLIENT_ID_PMAPP_GA = 3; // Official Google Actions Client
    public static final String GOOGLE_API_KEY = System.getenv("PMAPI_GOOGLE_API_KEY");
}
