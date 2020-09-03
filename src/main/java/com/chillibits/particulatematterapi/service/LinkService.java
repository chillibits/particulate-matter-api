/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCode;
import com.chillibits.particulatematterapi.exception.exception.LinkDataException;
import com.chillibits.particulatematterapi.model.db.main.Link;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.dto.LinkDto;
import com.chillibits.particulatematterapi.model.dto.LinkInsertUpdateDto;
import com.chillibits.particulatematterapi.repository.LinkRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private ModelMapper mapper;

    public LinkDto addLink(LinkInsertUpdateDto link) throws LinkDataException {
        // Check for possible faulty data parameters
        validateLinkObject(link);

        // Add additional data
        Sensor sensor = sensorRepository.getOne(link.getSensor().getChipId());
        Link linkDbo = convertToDbo(link);
        linkDbo.setSensor(sensor);
        linkDbo.setCreationTimestamp(System.currentTimeMillis());
        return convertToDto(linkRepository.save(linkDbo));
    }

    public Integer updateLink(LinkInsertUpdateDto link) throws LinkDataException {
        validateLinkObject(link);
        return linkRepository.updateLink(convertToDbo(link));
    }

    public void deleteLinkById(Integer id) {
        linkRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    public LinkDto convertToDto(Link link) {
        return mapper.map(link, LinkDto.class);
    }

    public Link convertToDbo(LinkInsertUpdateDto linkDto) {
        return mapper.map(linkDto, Link.class);
    }

    private void validateLinkObject(LinkInsertUpdateDto link) throws LinkDataException {
        if(link.getName().isBlank()) throw new LinkDataException(ErrorCode.INVALID_LINK_DATA);
        if(sensorRepository.findById(link.getSensor().getChipId()).isEmpty()) throw new LinkDataException(ErrorCode.SENSOR_NOT_EXISTING);
        if(userRepository.findById(link.getUser().getId()).isEmpty()) throw new LinkDataException(ErrorCode.USER_NOT_EXISTING);
    }
}