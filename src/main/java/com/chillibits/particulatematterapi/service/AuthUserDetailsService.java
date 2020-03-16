/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.io.AuthUserDetails;
import com.chillibits.particulatematterapi.repository.main.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@AllArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<Client> user = clientRepository.findByName(name);
        user.orElseThrow(() -> new UsernameNotFoundException("User " + name + " not found"));
        return user.map(AuthUserDetails::new).get();
    }
}