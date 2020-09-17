/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.service;

import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ActiveProfiles("logging")
@DisplayName("Auth Details Service")
public class AuthDetailsServiceTests {

    @Autowired
    private AuthDetailsService authDetailsService;
    @MockBean
    private ClientRepository clientRepository;

    @TestConfiguration
    static class AuthDetailsControllerImplTestContextConfiguration {

        @Autowired
        private ClientRepository clientRepository;

        @Bean
        public AuthDetailsService authDetailsService() {
            return new AuthDetailsService(clientRepository);
        }
    }

    @Before
    public void init() {
        // Setup fake method calls
        when(clientRepository.findByName("pmapp")).thenReturn(Optional.of(getTestClient()));
        when(clientRepository.findByName("pmapp-web")).thenReturn(Optional.empty());
    }

    // -------------------------------------------------- Test data ----------------------------------------------------

    @Test
    public void testGetAuthDetailsSuccessful() {
        UserDetails result = authDetailsService.loadUserByUsername("pmapp");
        assertEquals("pmapp", result.getUsername());
        assertEquals("1234567890", result.getPassword());
        assertThat(result.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .containsExactlyInAnyOrder("A", "CBA");
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
    }

    @Test
    public void testGetAuthDetailsFailure() {
        // Try with invalid input
        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                authDetailsService.loadUserByUsername("pmapp-web")
        );

        String expectedMessage = new UsernameNotFoundException("User pmapp-web not found").getMessage();
        assertEquals(expectedMessage, exception.getMessage());
    }

    // ---------------------------------------------------- Tests ------------------------------------------------------

    private Client getTestClient() {
        return new Client(1, "pmapp", "Particluate Matter App", "1234567890",
                Client.TYPE_ANDROID_APP, Client.ROLE_APPLICATION + "," + Client.ROLE_APPLICATION_CHILLIBITS,
                Client.STATUS_ONLINE, true, 400, "4.0.0", 400,
                "4.0.0", "ChilliBits", "");
    }
}