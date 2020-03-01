/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Constants
    private final String ADMIN_APPLICATION_ROLE = "ADMIN_APPLICATION";
    private final String APPLICATION_ROLE = "APPLICATION";

    // Variables as objects
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Sensors endpoint
                .antMatchers(HttpMethod.GET, "/sensor").permitAll()
                .antMatchers(HttpMethod.POST, "/sensor").hasAnyRole(ADMIN_APPLICATION_ROLE, APPLICATION_ROLE)
                .antMatchers(HttpMethod.PUT, "/sensor").hasAnyRole(ADMIN_APPLICATION_ROLE, APPLICATION_ROLE)
                .antMatchers(HttpMethod.DELETE, "/sensor/**").hasRole(ADMIN_APPLICATION_ROLE)
                // Data endpoint
                .antMatchers(HttpMethod.GET, "/data").permitAll()
                .antMatchers(HttpMethod.POST, "/data").hasAnyRole(ADMIN_APPLICATION_ROLE, APPLICATION_ROLE)
                // ClientInto endpoint
                .antMatchers(HttpMethod.GET, "/info").permitAll()
                .antMatchers(HttpMethod.POST, "/info").hasRole(ADMIN_APPLICATION_ROLE)
                .antMatchers(HttpMethod.PUT, "/info").hasRole(ADMIN_APPLICATION_ROLE)
                // User endpoint
                .antMatchers("/user").permitAll()
                .and().httpBasic();

    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}