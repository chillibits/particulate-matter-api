/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.ClientInfo;
import com.chillibits.particulatematterapi.repository.ClientInfoRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
public class ClientInfoController {

    @Autowired
    ClientInfoRepository clientInfoRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client info objects, found in the database")
    public List<ClientInfo> getClientInfo() {
        return clientInfoRepository.findAll();
    }

    /*@RequestMapping(method = RequestMethod.GET, path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientInfo getClientInfo(@RequestParam Integer type) {
        for(ClientInfo info : clientInfoRepository.findAll()) {
            if(info.getClientType() == type) return info;
        }
        return null;
    }*/

    @RequestMapping(method = RequestMethod.POST, path = "/info", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client info object")
    public ClientInfo addClientInfo(@RequestBody ClientInfo info) {
        return clientInfoRepository.save(info);
    }

    @Transactional
    @RequestMapping(method = RequestMethod.PUT, path = "/info", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client info object")
    public Integer updateClientInfoAndroid(@RequestBody ClientInfo info) {
        return clientInfoRepository.updateClientInfo(info.getId(), info.getServerStatus(), info.getMinVersion(), info.getMinVersionName(), info.getLatestVersion(), info.getLatestVersionName(), info.getServerOwner(), info.getUserMessage());
    }
}