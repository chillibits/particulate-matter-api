/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.io.ClientInfo;
import com.chillibits.particulatematterapi.repository.main.ClientRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Client REST Endpoint", tags = { "client" })
public class ClientController {
    private ClientRepository clientRepository;

    @RequestMapping(method = RequestMethod.GET, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client objects, found in the database")
    public List<ClientInfo> getClientInfo() {
        return clientRepository.findAllClients();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/client/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getClientInfo(@PathVariable("name") String name) {
        return clientRepository.findClientByName(name);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object")
    public Client addClientInfo(@RequestBody Client info) {
        return clientRepository.save(info);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object")
    public void updateClientInfoAndroid(@RequestBody Client info) {
        clientRepository.updateClient(
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
    }
}