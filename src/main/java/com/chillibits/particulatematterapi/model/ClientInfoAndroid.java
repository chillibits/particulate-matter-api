/*
 * Copyright © 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.model;

import javax.persistence.*;

@Entity
@Table(name = "client_info_android")
public class ClientInfoAndroid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String clientName;
    private Integer serverStatus;
    private Integer minAppVersion;
    private String minAppVersionName;
    private Integer latestAppVersion;
    private String latestAppVersionName;
    private String serverOwner;
    private String userMessage;

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

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getServerStatus() {
        return serverStatus;
    }
    public void setServerStatus(Integer serverStatus) {
        this.serverStatus = serverStatus;
    }

    public Integer getMinAppVersion() {
        return minAppVersion;
    }
    public void setMinAppVersion(Integer minAppVersion) {
        this.minAppVersion = minAppVersion;
    }

    public String getMinAppVersionName() {
        return minAppVersionName;
    }
    public void setMinAppVersionName(String minAppVersionName) {
        this.minAppVersionName = minAppVersionName;
    }

    public Integer getLatestAppVersion() {
        return latestAppVersion;
    }
    public void setLatestAppVersion(Integer latestAppVersion) {
        this.latestAppVersion = latestAppVersion;
    }

    public String getLatestAppVersionName() {
        return latestAppVersionName;
    }
    public void setLatestAppVersionName(String latestAppVersionName) {
        this.latestAppVersionName = latestAppVersionName;
    }

    public String getServerOwner() {
        return serverOwner;
    }
    public void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
    }

    public String getUserMessage() {
        return userMessage;
    }
    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
}