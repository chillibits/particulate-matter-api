package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.db.Client;
import com.chillibits.particulatematterapi.model.io.AuthUserDetails;
import com.chillibits.particulatematterapi.repository.main.ClientRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class AuthUserDetailsService implements UserDetailsService {
    ClientRepository clientRepository;

    public AuthUserDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<Client> user = clientRepository.findByName(name);
        user.orElseThrow(() -> new UsernameNotFoundException("User " + name + " not found"));
        return user.map(AuthUserDetails::new).get();
    }
}