/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.data;

import com.chillibits.particulatematterapi.shared.ConstantUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class DataRecord {
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