package com.chillibits.particulatematterapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataRecord {
    // Attributes
    @Id
    private int id;
    private String time;
    private double pm2_5;
    private double pm10;
    private double temp;
    private double humidity;
    private double pressure;
    private double gps_lat;
    private double gps_lng;
    private double gps_alt;
    private String note;
}