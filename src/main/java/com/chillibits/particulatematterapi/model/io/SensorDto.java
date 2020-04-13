/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorDto {
    private long chipId;
    private String firmwareVersion;
    private long creationTimestamp;
    private String notes;
    private long lastMeasurementTimestamp;
    private long lastEditTimestamp;
    private double gpsLatitude;
    private double gpsLongitude;
    private double gpsAltitude;
    private String country;
    private String city;
    private boolean indoor;
    private boolean published;
}