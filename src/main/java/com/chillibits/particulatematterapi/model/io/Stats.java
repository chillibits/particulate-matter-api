/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    private int sensorsTotal;
    private int sensorsActive;
    private int sensorsMapTotal;
    private int sensorsMapActive;
    private int serverRequestsTotal;
    private int serverRequestsTodayApp;
    private int serverRequestsTodayWebApp;
    private int serverRequestsTodayGoogleActions;
    private int serverRequestsYesterdayApp;
    private int serverRequestsYesterdayWebApp;
    private int serverRequestsYesterdayGoogleActions;
    private long dataRecordsTotal;
    private long dataRecordsThisMonth;
    private long dataRecordsPrevMonth;
    private long dataRecordsToday;
    private long dataRecordsYesterday;
}