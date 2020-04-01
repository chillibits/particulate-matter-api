/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dbold;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OldSensor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chipId;
    private String firmwareVersion;
    private long creationDate;
    private String notes;
    private long lastUpdate;
    private long lastEdit;
    private String lat;
    private String lng;
    private String alt;
    private String country;
    private String city;
    private String mapsUrl;
    private double lastValue;
    private double lastValue_2;
}