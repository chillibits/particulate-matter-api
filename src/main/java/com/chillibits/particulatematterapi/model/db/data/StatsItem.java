/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsItem {
    private long chipId;
    private long timestamp;
    private long sensorsTotal;
    private long sensorsMapTotal;
    private long sensorsMapActive;
    private long serverRequestsTotal;
    private long serverRequestsTodayApp;
    private long serverRequestsTodayWebApp;
    private long serverRequestsTodayGoogleActions;
    private long serverRequestsYesterdayApp;
    private long serverRequestsYesterdayWebApp;
    private long serverRequestsYesterdayGoogleActions;
    private long dataRecordsTotal;
    private long dataRecordsThisMonth;
    private long dataRecordsPrevMonth;
    private long dataRecordsToday;
    private long dataRecordsYesterday;
}