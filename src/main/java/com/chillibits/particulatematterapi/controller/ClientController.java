/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller;

import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.io.ClientDto;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Api(value = "Client REST Endpoint", tags = "client")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ModelMapper mapper;

    @RequestMapping(method = RequestMethod.GET, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client objects, found in the database")
    public List<ClientDto> getClientInfo() {
        return clientRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/client/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClientDto getClientInfo(@PathVariable("name") String name) {
        Optional<Client> client = clientRepository.findByName(name);
        return client.map(this::convertToDto).orElse(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object", hidden = true)
    public Client addClientInfo(@RequestBody Client info) {
        return clientRepository.save(info);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object", hidden = true)
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

    @RequestMapping(method = RequestMethod.DELETE, path = "/client/{id}")
    @ApiOperation(value = "Deletes an user from the database", hidden = true)
    public void deleteClient(@PathVariable int id) {
        clientRepository.deleteById(id);
    }

    private ClientDto convertToDto(Client client) {
        return mapper.map(client, ClientDto.class);
    }

    private Client convertToEntity(ClientDto clientDto) {
        return mapper.map(clientDto, Client.class);
    }
}