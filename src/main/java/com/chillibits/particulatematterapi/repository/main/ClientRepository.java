package com.chillibits.particulatematterapi.repository.main;

import com.chillibits.particulatematterapi.model.db.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ClientRepository extends MongoRepository<Client, Integer> {
    Optional<Client> findByName(String name);

    Client getClientByName(String name);
}