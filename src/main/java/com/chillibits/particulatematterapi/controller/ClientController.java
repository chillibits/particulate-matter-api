/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.Client;
import com.chillibits.particulatematterapi.repository.main.ClientRepository;
import com.chillibits.particulatematterapi.service.IdSequenceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Client REST Endpoint", tags = { "client" })
public class ClientController {
    ClientRepository clientRepository;
    IdSequenceService idSequenceService;

    @RequestMapping(method = RequestMethod.GET, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client objects, found in the database")
    public List<Client> getClientInfo() {
        return clientRepository.findAll();
    }

    /*@RequestMapping(method = RequestMethod.GET, path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientInfo getClientInfo(@RequestParam Integer type) {
        for(ClientInfo info : clientInfoRepository.findAll()) {
            if(info.getClientType() == type) return info;
        }
        return null;
    }*/

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object")
    public Client addClientInfo(@RequestBody Client info) {
        info.setId(idSequenceService.getNextSequence("client"));
        return clientRepository.save(info);
    }

    /*@RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object")
    public Integer updateClientInfoAndroid(@RequestBody Client info) {
        return clientRepository.updateClient(
                info.getId(),
                info.getName(),
                info.getReadableName(),
                info.getSecret(),
                info.getType(),
                info.getRoles(),
                info.getStatus(),
                info.isActive(),
                info.getMinVersion(),
                info.getMinVersionName(),
                info.getLatestVersion(),
                info.getLatestVersionName(),
                info.getOwner(),
                info.getUserMessage()
        );
    }*/
}