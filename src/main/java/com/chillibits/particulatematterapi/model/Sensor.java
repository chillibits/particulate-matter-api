/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private int chipId;
    private int userId;
    private String firmwareVersion;
    @CreationTimestamp
    private LocalDateTime creationDate;
    private String notes;
    private LocalDateTime lastMeasurement;
    @UpdateTimestamp
    private LocalDateTime lastEdit;
    private double gpsLatitude;
    private double gpsLongitude;
    private double gpsAltitude;
    private String country;
    private String city;
    private String mapsUrl;
    private double lastValueP1;
    private double lastValueP2;
}