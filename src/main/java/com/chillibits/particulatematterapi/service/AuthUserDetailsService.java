/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.AuthUserDetails;
import com.chillibits.particulatematterapi.model.Client;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Primary
@Service
public class AuthUserDetailsService implements UserDetailsService {
    @Autowired
    ClientRepository authUserReporitory;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<Client> user = authUserReporitory.findByName(name);
        user.orElseThrow(() -> new UsernameNotFoundException("User " + name + " not found"));
        return user.map(AuthUserDetails::new).get();
    }
}