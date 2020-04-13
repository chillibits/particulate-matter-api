/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private String firstName;
    private String lastName;
    private Set<LinkDto> sensorLinks;
    private int role;
    private int status;
    private long creationTimestamp;
    private long lastEditTimestamp;
}
