/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.ClientInfo;
import com.chillibits.particulatematterapi.repository.ClientInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
public class ClientInfoController {

    @Autowired
    ClientInfoRepository clientInfoRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientInfo getClientInfoAndroid() {
        return clientInfoRepository.findAll().isEmpty() ? null : clientInfoRepository.findAll().get(0);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ClientInfo addClientInfo(@RequestBody ClientInfo info) {
        return clientInfoRepository.save(info);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, path = "/info", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Integer updateClientInfoAndroid(@RequestBody ClientInfo info) {
        return clientInfoRepository.updateClientInfoAndroid(info.getId(), info.getServerStatus(), info.getMinVersion(), info.getMinVersionName(), info.getLatestVersion(), info.getLatestVersionName(), info.getServerOwner(), info.getUserMessage());
    }
}