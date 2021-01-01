/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataRecordInsertUpdateDto {

    @JsonProperty("esp8266id") private long chipId;
    private long timestamp = 0;
    @JsonProperty("software_version") private String firmwareVersion;
    @JsonProperty("sensordatavalues") private SensorDataValue[] sensorDataValues;
    private String note = ConstantUtils.BLANK_COLUMN;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SensorDataValue {
        @JsonProperty("value_type") private String valueType;
        @JsonProperty("value") private double value;
    }
}