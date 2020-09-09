/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

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

/**
 * Client endpoint
 *
 * A client is a data record, which represents an application with access to the Particulate Matter API.
 * For each client, there are one or several roles defined. These roles limit the access permissions of the application.
 * Clients can only to be registered by the ChilliBits team. Please contact us via email (contact@chillibits.com)
 */
@RestController
@Api(value = "Client REST Endpoint", tags = "client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * Returns all client objects, found in the database
     *
     * @return List of all clients
     */
    @RequestMapping(method = RequestMethod.GET, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns all client objects, found in the database")
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients();
    }

    /**
     * Returns info about a specific client by its name
     *
     * @return Client info as a ClientDto object
     */
    @RequestMapping(method = RequestMethod.GET, path = "/client/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns info about a specific client by its name")
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "This client does not exist")
    })
    public ClientDto getClientInfoByName(@PathVariable("name") String name) {
        return clientService.getClientByName(name);
    }

    /**
     * Adds a client object
     * <p>Note: Requires application role AA (Admin Application)</p>
     *
     * @return Client info as a ClientDto object
     */
    @RequestMapping(method = RequestMethod.POST, path = "/client", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Adds a client object", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide a client object with all fields filled")
    })
    public ClientDto addClient(@RequestBody ClientInsertUpdateDto client) {
        return clientService.addClient(client);
    }

    /**
     * Updates a specific client object
     * <p>Note: Requires application role AA (Admin Application)</p>
     *
     * @return Client info as a ClientDto object
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/client", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a specific client object", hidden = true)
    @ApiResponses(value = {
            @ApiResponse(code = 406, message = "Please provide a client object with all fields filled")
    })
    public Integer updateClient(@RequestBody ClientInsertUpdateDto client) {
        return clientService.updateClient(client);
    }

    /**
     * Deletes an user from the database
     * <p>Note: Requires application role AA (Admin Application)</p>
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/client/{id}")
    @ApiOperation(value = "Deletes an user from the database", hidden = true)
    public void deleteClient(@PathVariable int id) {
        clientService.deleteClientById(id);
    }
}