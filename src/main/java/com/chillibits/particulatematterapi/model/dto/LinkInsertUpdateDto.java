/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkInsertUpdateDto {
    private Integer id;
    private UserDto user;
    private SensorDto sensor;
    private boolean owner;
    private String name;
    private int color;
}
