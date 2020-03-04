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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Constants
    private final String ADMIN_APPLICATION_ROLE = "ADMIN_APPLICATION";
    private final String APPLICATION_ROLE = "APPLICATION";

    // Variables as objects
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSouce;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            // Sensors endpoint
            .antMatchers(HttpMethod.GET, "/sensor").permitAll()
            .antMatchers(HttpMethod.POST, "/sensor").hasAnyAuthority(ADMIN_APPLICATION_ROLE, APPLICATION_ROLE)
            .antMatchers(HttpMethod.PUT, "/sensor").hasAnyAuthority(ADMIN_APPLICATION_ROLE, APPLICATION_ROLE)
            .antMatchers(HttpMethod.DELETE, "/sensor/**").hasAuthority(ADMIN_APPLICATION_ROLE)
            // Ranking endpoint
            .antMatchers(HttpMethod.GET, "/ranking/**").permitAll()
            // Data endpoint
            .antMatchers(HttpMethod.GET, "/data").permitAll()
            .antMatchers(HttpMethod.POST, "/data").hasAnyAuthority(ADMIN_APPLICATION_ROLE, APPLICATION_ROLE)
            // ClientInto endpoint
            .antMatchers(HttpMethod.GET, "/info").permitAll()
            .antMatchers(HttpMethod.POST, "/info").hasAuthority(ADMIN_APPLICATION_ROLE)
            .antMatchers(HttpMethod.PUT, "/info").hasAuthority(ADMIN_APPLICATION_ROLE)
            // User endpoint
            .antMatchers("/user").permitAll()
            .and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().httpBasic();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}