/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.model;

import javax.persistence.*;

@Entity
@Table(name = "client_info")
public class ClientInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String clientName;
    private Integer serverStatus;
    private Integer minVersion;
    private String minVersionName;
    private Integer latestVersion;
    private String latestVersionName;
    private String serverOwner;
    private String userMessage;

    public ClientInfo() {}

    public ClientInfo(String clientName, Integer serverStatus, Integer minVersion, String minVersionName, Integer latestVersion, String latestVersionName, String serverOwner, String userMessage) {
        this.clientName = clientName;
        this.serverStatus = serverStatus;
        this.minVersion = minVersion;
        this.minVersionName = minVersionName;
        this.latestVersion = latestVersion;
        this.latestVersionName = latestVersionName;
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

    public Integer getMinVersion() {
        return minVersion;
    }
    public void setMinVersion(Integer minVersion) {
        this.minVersion = minVersion;
    }

    public String getMinVersionName() {
        return minVersionName;
    }
    public void setMinVersionName(String minAppVersionName) {
        this.minVersionName = minAppVersionName;
    }

    public Integer getLatestVersion() {
        return latestVersion;
    }
    public void setLatestVersion(Integer latestAppVersion) {
        this.latestVersion = latestAppVersion;
    }

    public String getLatestVersionName() {
        return latestVersionName;
    }
    public void setLatestVersionName(String latestAppVersionName) {
        this.latestVersionName = latestAppVersionName;
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