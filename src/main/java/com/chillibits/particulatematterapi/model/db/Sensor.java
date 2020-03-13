package com.chillibits.particulatematterapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private int chipId;
    private int userId;
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
    private String mapsUrl;
    private double lastValueP1;
    private double lastValueP2;
}