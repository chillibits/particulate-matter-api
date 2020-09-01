/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.exception.ClientDataException;
import com.chillibits.particulatematterapi.model.dto.ClientDto;
import com.chillibits.particulatematterapi.model.dto.ClientInsertUpdateDto;
import com.chillibits.particulatematterapi.service.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "Client REST Endpoint", tags = "client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @RequestMapping(method = RequestMethod.GET, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client objects, found in the database")
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/client/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns info about a specific client, identified by its name")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "This client does not exist")
    })
    public ClientDto getClientInfoByName(@PathVariable("name") String name) {
        return clientService.getClientByName(name);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide a client object with all fields filled")
    })
    public ClientDto addClient(@RequestBody ClientInsertUpdateDto client) throws ClientDataException {
        return clientService.addClient(client);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide a client object with all fields filled")
    })
    public Integer updateClient(@RequestBody ClientInsertUpdateDto client) throws ClientDataException {
        return clientService.updateClient(client);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/client/{id}")
    @ApiOperation(value = "Deletes an user from the database", hidden = true)
    public void deleteClient(@PathVariable int id) {
        clientService.deleteClientById(id);
    }
}