/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInsertUpdateDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<LinkDto> sensorLinks;
    private int role;
    private int status;
}
