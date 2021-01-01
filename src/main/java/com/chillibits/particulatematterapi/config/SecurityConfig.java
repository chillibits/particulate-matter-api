/*
 * Copyright © Marc Auberer 2019-2021. All rights reserved
 */

package com.chillibits.particulatematterapi.config;

import com.chillibits.particulatematterapi.model.db.main.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Qualifier("authDetailsService")
    private UserDetailsService userDetailsService; // Has to be auto wired. Without it, there is a dependency cycle

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // Swagger page
                .antMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                // Sensors endpoint
                .antMatchers(HttpMethod.GET, "/sensor").permitAll()
                .antMatchers(HttpMethod.POST, "/sensor").hasAnyAuthority(Client.ROLE_APPLICATION, Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.PUT, "/sensor").hasAnyAuthority(Client.ROLE_APPLICATION, Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/sensor/**").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
                // Ranking endpoint
                .antMatchers(HttpMethod.GET, "/ranking/**").permitAll()
                // Data endpoint
                .antMatchers(HttpMethod.GET, "/data/**").permitAll()
                // Push endpoint
                .antMatchers(HttpMethod.POST, "/push").permitAll()
                // Chart endpoint
                .antMatchers(HttpMethod.GET, "/chart/**").permitAll()
                // Confirm endpoint
                .antMatchers(HttpMethod.GET, "/confirm").permitAll()
                // User endpoint
                .antMatchers(HttpMethod.GET, "/user/**").hasAnyAuthority(Client.ROLE_APPLICATION, Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.GET, "/user").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.POST, "/user").hasAnyAuthority(Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.PUT, "/user").hasAnyAuthority(Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/user").hasAnyAuthority(Client.ROLE_APPLICATION_CHILLIBITS, Client.ROLE_APPLICATION_ADMIN)
                // Client endpoint
                .antMatchers(HttpMethod.POST, "/client").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.PUT, "/client").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.DELETE, "/client").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
                .antMatchers(HttpMethod.GET, "/client").permitAll()
                // Link endpoint
                .antMatchers(HttpMethod.GET, "/link").hasAuthority(Client.ROLE_APPLICATION)
                .antMatchers(HttpMethod.POST, "/link").hasAuthority(Client.ROLE_APPLICATION)
                .antMatchers(HttpMethod.PUT, "/link").hasAuthority(Client.ROLE_APPLICATION)
                .antMatchers(HttpMethod.DELETE, "/link").hasAuthority(Client.ROLE_APPLICATION)
                // Log endpoint
                .antMatchers(HttpMethod.GET, "/log/**").hasAuthority(Client.ROLE_APPLICATION_ADMIN)
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