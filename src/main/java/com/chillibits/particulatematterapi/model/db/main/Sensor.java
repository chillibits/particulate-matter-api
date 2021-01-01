/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.main;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Entity
@Table(name = "sensor")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="chipId")
public class Sensor {
    @Id
    private long chipId;
    @OneToMany(mappedBy = "sensor")
    private Set<Link> userLinks;
    private String firmwareVersion;
    private long creationTimestamp;
    private String notes;
    private long lastMeasurementTimestamp;
    private long lastEditTimestamp;
    private double gpsLatitude;
    private double gpsLongitude;
    private int gpsAltitude;
    private String country;
    private String city;
    private boolean indoor;
    private boolean published;
    @Transient
    private boolean active;
}