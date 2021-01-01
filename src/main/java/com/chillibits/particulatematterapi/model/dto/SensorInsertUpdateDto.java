/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorInsertUpdateDto {
    private long chipId;
    private Set<LinkInsertUpdateDto> userLinks;
    private double gpsLatitude;
    private double gpsLongitude;
    private int gpsAltitude;
    private boolean indoor;
    private boolean published;
}