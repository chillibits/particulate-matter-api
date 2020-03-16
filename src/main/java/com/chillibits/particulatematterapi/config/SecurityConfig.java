/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.config;

import com.chillibits.particulatematterapi.model.db.main.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService; // Has to be autowired. Without it, there is a dependency cycle

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Sensors endpoint
                .antMatchers(HttpMethod.GET, "/sensor").permitAll()
                .antMatchers(HttpMethod.POST, "/sensor").hasAnyAuthority(Client.ROLE_APPLICATION, Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.PUT, "/sensor").hasAnyAuthority(Client.ROLE_APPLICATION, Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/sensor/**").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
                // Ranking endpoint
                .antMatchers(HttpMethod.GET, "/ranking/**").permitAll()
                // Data endpoint
                .antMatchers(HttpMethod.GET, "/data").permitAll()
                .antMatchers(HttpMethod.POST, "/data").hasAnyAuthority(Client.ROLE_APPLICATION_ADMIN)
                // Push endpoint
                .antMatchers(HttpMethod.POST, "/push").permitAll()
                // User endpoint
                .antMatchers(HttpMethod.GET, "/user").hasAnyAuthority(Client.ROLE_APPLICATION, Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.POST, "/user").hasAnyAuthority(Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.PUT, "/user").hasAnyAuthority(Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                // Client endpoint
                .antMatchers("/client").permitAll()
                //.antMatchers("/client").hasAuthority(Client.APPLICATION_ADMIN)
                // Stats endpoint
                .antMatchers("/stats").permitAll()
                .and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().httpBasic();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}