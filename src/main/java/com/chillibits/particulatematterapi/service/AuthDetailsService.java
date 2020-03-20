/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.io.AuthDetails;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class AuthDetailsService implements UserDetailsService {
    ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<Client> user = clientRepository.findByName(name);
        user.orElseThrow(() -> new UsernameNotFoundException("User " + name + " not found"));
        return user.map(AuthDetails::new).get();
    }
}