/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;

@Entity
@Table(name = "client_info_android")
public class ClientInfoAndroid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NonNull private String clientName;
    @NonNull private Integer serverStatus;
    @NonNull private Integer minAppVersion;
    @NonNull private String minAppVersionName;
    @NonNull private Integer latestAppVersion;
    @NonNull private String latestAppVersionName;
    @NonNull private String serverOwner;
    @NonNull private String userMessage;

    public ClientInfoAndroid() {}

    public ClientInfoAndroid(String clientName, Integer serverStatus, Integer minAppVersion, String minAppVersionName, Integer latestAppVersion, String latestAppVersionName, String serverOwner, String userMessage) {
        this.clientName = clientName;
        this.serverStatus = serverStatus;
        this.minAppVersion = minAppVersion;
        this.minAppVersionName = minAppVersionName;
        this.latestAppVersion = latestAppVersion;
        this.latestAppVersionName = latestAppVersionName;
        this.serverOwner = serverOwner;
        this.userMessage = userMessage;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getClientName() {
        return clientName;
    }
    public void setClientName(@NonNull String clientName) {
        this.clientName = clientName;
    }

    @NonNull
    public Integer getServerStatus() {
        return serverStatus;
    }
    public void setServerStatus(@NonNull Integer serverStatus) {
        this.serverStatus = serverStatus;
    }

    @NonNull
    public Integer getMinAppVersion() {
        return minAppVersion;
    }
    public void setMinAppVersion(@NonNull Integer minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    @NonNull
    public String getMinAppVersionName() {
        return minAppVersionName;
    }
    public void setMinAppVersionName(@NonNull String minAppVersionName) {
        this.minAppVersionName = minAppVersionName;
    }

    @NonNull
    public Integer getLatestAppVersion() {
        return latestAppVersion;
    }
    public void setLatestAppVersion(@NonNull Integer latestAppVersion) {
        this.latestAppVersion = latestAppVersion;
    }

    @NonNull
    public String getLatestAppVersionName() {
        return latestAppVersionName;
    }
    public void setLatestAppVersionName(@NonNull String latestAppVersionName) {
        this.latestAppVersionName = latestAppVersionName;
    }

    @NonNull
    public String getServerOwner() {
        return serverOwner;
    }
    public void setServerOwner(@NonNull String serverOwner) {
        this.serverOwner = serverOwner;
    }

    @NonNull
    public String getUserMessage() {
        return userMessage;
    }
    public void setUserMessage(@NonNull String userMessage) {
        this.userMessage = userMessage;
    }
}
