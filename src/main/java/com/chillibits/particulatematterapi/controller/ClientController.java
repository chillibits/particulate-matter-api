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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Api(value = "Client REST Endpoint", tags = { "client" })
public class ClientController {
    ClientRepository clientRepository;
    IdSequenceService idSequenceService;
    MongoTemplate mongoTemplate;

    @RequestMapping(method = RequestMethod.GET, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client objects, found in the database")
    public List<Client> getClientInfo() {
        return clientRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/client/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getClientInfo(@PathVariable("name") String name) {
        return clientRepository.findByName(name).orElse(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object")
    public Client addClientInfo(@RequestBody Client info) {
        return clientRepository.insert(info);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object")
    public void updateClientInfoAndroid(@RequestBody Client info) {
        clientRepository.save(info);
    }
}