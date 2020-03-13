package com.chillibits.particulatematterapi.repository.main;

import com.chillibits.particulatematterapi.model.db.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Integer> {
    //Integer updateUser(int id, String firstName, String lastName, String password, int role, int status);
}