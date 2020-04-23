/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorCompressedDto {
    @JsonProperty("i") private long chipId;
    @JsonProperty("la") private double gpsLatitude;
    @JsonProperty("lo") private double gpsLongitude;
    @JsonProperty("a") private boolean active;
}