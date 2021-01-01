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
public class ClientInsertUpdateDto {
    private Integer id;
    private String name;
    private String readableName;
    private String secret;
    private int type;
    private String roles;
    private int status;
    private boolean active;
    private int minVersion;
    private String minVersionName;
    private int latestVersion;
    private String latestVersionName;
    private String owner;
    private String userMessage;
}