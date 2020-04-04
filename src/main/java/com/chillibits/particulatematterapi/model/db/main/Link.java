/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {
    @Id
    private int id;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("sensor_id")
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    private boolean owner;
    private String name;
    private int color;
    private long creationTimestamp;
}