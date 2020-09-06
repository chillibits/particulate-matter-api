/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import com.chillibits.particulatematterapi.shared.ConstantUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class DataRecordDto implements Comparable<DataRecordDto> {

    private long timestamp = 0;
    private String firmwareVersion;
    private SensorDataValue[] sensorDataValues;
    private String note = ConstantUtils.BLANK_COLUMN;

    @Override
    public int compareTo(DataRecordDto other) {
        return Long.compare(timestamp, other.timestamp);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SensorDataValue {
        private String valueType;
        private double value;
    }
}