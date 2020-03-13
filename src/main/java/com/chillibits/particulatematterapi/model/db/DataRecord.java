package com.chillibits.particulatematterapi.model.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataRecord {
    @JsonProperty("esp8266id") private long chipId;
    private long timestamp;
    @JsonProperty("software_version") private String firmwareVersion;
    @JsonProperty("sensordatavalues") private SensorDataValues[] sensorDataValues;
    private String note;
}

@Data
class SensorDataValues {
    @JsonProperty("value_type") private String valueType;
    @JsonProperty("value") private String value;
}