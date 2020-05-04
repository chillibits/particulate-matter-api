/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.controller.v1;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.LinkDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "Link REST Endpoint", tags = "link")
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SensorRepository sensorRepository;

    @RequestMapping(method = RequestMethod.POST, path = "/link", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, params = "chipId")
    @ApiOperation(value = "Adds a link to the database")
    public Link addLink(@RequestBody Link link, @RequestParam Long chipId) throws LinkDataException {
        // Check for possible faulty data parameters
        if(sensorRepository.findById(chipId).isEmpty()) throw new LinkDataException(ErrorCodeUtils.SENSOR_NOT_EXISTING);
        validateLinkObject(link);

        link.setSensor(sensorRepository.getOne(chipId));
        link.setCreationTimestamp(System.currentTimeMillis());
        return linkRepository.save(link);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/link", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Updates a link")
    public Integer updateLink(@RequestBody Link link) throws LinkDataException {
        validateLinkObject(link);
        return linkRepository.updateLink(link.getId(), link.isOwner(), link.getName(), link.getColor());
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/link/{id}")
    @ApiOperation(value = "Deletes a link from the database")
    public void deleteLink(@PathVariable int id) {
        linkRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    private void validateLinkObject(Link link) throws LinkDataException {
        if(link.getName().isBlank()) throw new LinkDataException(ErrorCodeUtils.INVALID_LINK_DATA);
        if(userRepository.findById(link.getUser().getId()).isEmpty()) throw new LinkDataException(ErrorCodeUtils.USER_NOT_EXISTING);
    }
}