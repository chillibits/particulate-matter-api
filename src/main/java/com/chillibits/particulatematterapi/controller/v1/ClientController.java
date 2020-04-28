/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ClientDataException;
import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.dto.ClientDto;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/client/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns info about a specific client, identified by its name")
    public ClientDto getClientInfoByName(@PathVariable("name") String name) throws ClientDataException {
        Optional<Client> client = clientRepository.findByName(name);
        if(client.isEmpty()) throw new ClientDataException(ErrorCodeUtils.CLIENT_NOT_EXISTING);
        return client.map(this::convertToDto).orElse(null);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object", hidden = true)
    public Client addClient(@RequestBody Client client) throws ClientDataException {
        validateClientObject(client);
        return clientRepository.save(client);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object", hidden = true)
    public Integer updateClient(@RequestBody Client client) throws ClientDataException {
        validateClientObject(client);
        return clientRepository.updateClient(client);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/client/{id}")
    @ApiOperation(value = "Deletes an user from the database", hidden = true)
    public void deleteClient(@PathVariable int id) {
        clientRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private ClientDto convertToDto(Client client) {
        return mapper.map(client, ClientDto.class);
    }

    private void validateClientObject(Client client) throws ClientDataException {
        if(client.getName().isBlank() || client.getLatestVersionName().isBlank() || client.getMinVersionName().isBlank() ||
                client.getRoles().isBlank() || client.getSecret().isBlank() || client.getOwner().isBlank() ||
                client.getReadableName().isBlank()) throw new ClientDataException(ErrorCodeUtils.INVALID_CLIENT_DATA);
    }
}