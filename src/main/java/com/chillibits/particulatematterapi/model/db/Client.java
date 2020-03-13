package com.chillibits.particulatematterapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

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
    private ObjectId id;
    private String name;
    private String readableName;
    private String secret;
    private int type;
    private String roles;
    private Integer status;
    private boolean active;
    private Integer minVersion;
    private String minVersionName;
    private Integer latestVersion;
    private String latestVersionName;
    private String owner;
    private String userMessage;

    public String getId() {
        return id.toString();
    }
}