/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.exception.ErrorCodeUtils;
import com.chillibits.particulatematterapi.exception.exception.ClientDataException;
import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.dto.ClientDto;
import com.chillibits.particulatematterapi.model.dto.ClientInsertUpdateDto;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ModelMapper mapper;

    public List<ClientDto> getAllClients() {
        return clientRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ClientDto getClientByName(String name) throws ClientDataException {
        Optional<Client> client = clientRepository.findByName(name);
        if(client.isEmpty()) throw new ClientDataException(ErrorCodeUtils.CLIENT_NOT_EXISTING);
        return client.map(this::convertToDto).orElse(null);
    }

    public ClientDto addClient(ClientInsertUpdateDto client) throws ClientDataException {
        validateClientObject(client);
        return convertToDto(clientRepository.save(convertToDbo(client)));
    }

    public Integer updateClient(ClientInsertUpdateDto client) throws ClientDataException {
        validateClientObject(client);
        return clientRepository.updateClient(convertToDbo(client));
    }

    public void deleteClientById(Integer id) {
        clientRepository.deleteById(id);
    }

    // ---------------------------------------------- Utility functions ------------------------------------------------

    public ClientDto convertToDto(Client client) {
        return mapper.map(client, ClientDto.class);
    }

    public Client convertToDbo(ClientInsertUpdateDto clientDto) {
        return mapper.map(clientDto, Client.class);
    }

    public void validateClientObject(ClientInsertUpdateDto client) throws ClientDataException {
        if(client.getName().isBlank() || client.getLatestVersionName().isBlank() || client.getMinVersionName().isBlank()
                || client.getRoles().isBlank() || client.getSecret().isBlank() || client.getOwner().isBlank()
                || client.getReadableName().isBlank()) throw new ClientDataException(ErrorCodeUtils.INVALID_CLIENT_DATA);
    }
}