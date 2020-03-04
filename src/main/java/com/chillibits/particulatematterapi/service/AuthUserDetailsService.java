/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.AuthUserDetails;
import com.chillibits.particulatematterapi.model.User;
import com.chillibits.particulatematterapi.repository.AuthUserRepository;
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
    AuthUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        System.out.println(username);
        System.out.println(user.get().isActive());
        user.orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
        return user.map(AuthUserDetails::new).get();
    }
}