/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.data;

import com.chillibits.particulatematterapi.shared.ConstantUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Transient;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class DataRecord {
    @Transient private long chipId;
    private long timestamp = 0;
    @Transient private String firmwareVersion;
    private SensorDataValue[] sensorDataValues;
    private String note = ConstantUtils.BLANK_COLUMN;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SensorDataValue {
        private String valueType;
        private double value;
    }
}