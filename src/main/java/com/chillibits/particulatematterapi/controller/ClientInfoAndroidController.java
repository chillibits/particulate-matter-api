/*
 * Copyright © 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.ClientInfoAndroid;
import com.chillibits.particulatematterapi.repository.ClientInfoAndroidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
public class ClientInfoAndroidController {

    @Autowired
    ClientInfoAndroidRepository clientInfoAndroidRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/android", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientInfoAndroid getClientInfoAndroid() {
        return clientInfoAndroidRepository.findAll().isEmpty() ? null : clientInfoAndroidRepository.findAll().get(0);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/android", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientInfoAndroid addClientInfo(@RequestBody ClientInfoAndroid info) {
        return clientInfoAndroidRepository.save(info);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, path = "/android", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Integer updateClientInfoAndroid(@RequestBody ClientInfoAndroid info) {
        return clientInfoAndroidRepository.updateClientInfoAndroid(info.getId(), info.getServerStatus(), info.getMinAppVersion(), info.getMinAppVersionName(), info.getLatestAppVersion(), info.getLatestAppVersionName(), info.getServerOwner(), info.getUserMessage());
    }
}