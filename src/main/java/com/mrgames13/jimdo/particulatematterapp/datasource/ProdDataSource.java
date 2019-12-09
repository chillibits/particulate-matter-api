/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Configuration
@Component
@Profile("prod")
public class ProdDataSource implements DataSource {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    @Override
    public javax.sql.DataSource getDataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:mysql://localhost:3306/sensors_prod?serverTimezone=UTC")
                .username(DataBaseCredentials.USERNAME)
                .password(DataBaseCredentials.PASSWORD)
                .build();
    }
}
