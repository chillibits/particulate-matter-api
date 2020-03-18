/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.io;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataRecordDto {
    @JsonProperty("t") private long timestamp = 0;
    @JsonProperty("d") private SensorDataValuesDto[] sensorDataValues;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensorDataValuesDto {
        @JsonProperty("t") private String valueType;
        @JsonProperty("v") private double value;
    }
}