/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.shared;

public class ConstantUtils {
    public static final String EMPTY_COLUMN = "-";
    public static final String BLANK_COLUMN = "";
    public static final long DEFAULT_DATA_TIME_SPAN = 24 * 60 * 60 * 1000; // One day
    public static final int MINUTES_UNTIL_INACTIVITY = 4320; // 3 days
    public static final int UNKNOWN_USER_ID = 1;
    public static final int UNKNOWN_CLIENT_ID = 1;
    public static final String LOG_TABLE_NAME = "_access_log";
    public static final boolean IMPORT_SENSORS_IF_TABLE_IS_EMPTY = true;
}
