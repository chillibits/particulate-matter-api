/*
 * Copyright © 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi.datasource;

import com.chillibits.particulatematterapi.shared.Credentials;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Profile("dev")
public class DevDataSource implements DataSource {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @Override
    public javax.sql.DataSource getDataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:mysql://localhost:3306/main?serverTimezone=UTC")
                .username(Credentials.USERNAME)
                .password(Credentials.PASSWORD)
                .build();
    }
}
