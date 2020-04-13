/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {
    private Integer id;
    private SensorDto sensor;
    private boolean owner;
    private String name;
    private int color;
    private long creationTimestamp;
}
