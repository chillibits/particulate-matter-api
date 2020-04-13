/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private String name;
    private String readableName;
    private int type;
    private int status;
    private int minVersion;
    private String minVersionName;
    private int latestVersion;
    private String latestVersionName;
    private String owner;
    private String userMessage;
}