/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataRecordCompressedDto implements Comparable<DataRecordCompressedDto> {

    @JsonProperty("t")
    private long timestamp = 0;
    @JsonProperty("d")
    private SensorDataValuesDto[] sensorDataValues;

    @Override
    public int compareTo(DataRecordCompressedDto other) {
        return Long.compare(timestamp, other.timestamp);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SensorDataValuesDto {
        @JsonProperty("t") private String valueType;
        @JsonProperty("v") private double value;
    }
}