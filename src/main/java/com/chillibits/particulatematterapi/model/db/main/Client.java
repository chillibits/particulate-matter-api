/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.model.db.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    // Constants
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_OFFLINE = 2;
    public static final int STATUS_MAINTENANCE = 3;
    public static final int STATUS_SUPPORT_ENDED = 4;

    public static final String ROLE_APPLICATION = "A";
    public static final String ROLE_APPLICATION_CHILLIBITS = "CBA";
    public static final String ROLE_APPLICATION_ADMIN = "AA";

    public static final int TYPE_WEBSITE = 1;
    public static final int TYPE_DESKTOP_APPLICATION = 2;
    public static final int TYPE_ANDROID_APP = 3;
    public static final int TYPE_IOS_APP = 4;
    public static final int TYPE_CROSS_PLATFORM_APP = 5;

    // Attributes
    @Id
    private int id;
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