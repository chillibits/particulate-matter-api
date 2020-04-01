/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {
    @Id
    private long chipId;
    @OneToMany(mappedBy = "sensor")
    private Set<UserSensorLink> userLinks;
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
}