/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfo {
    @Id
    private int id;
    private String name;
    private String readableName;
    private int type;
    private Integer status;
    private Integer minVersion;
    private String minVersionName;
    private Integer latestVersion;
    private String latestVersionName;
    private String owner;
    private String userMessage;
}