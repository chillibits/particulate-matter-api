/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp.datasource;

import com.mrgames13.jimdo.particulatematterapp.tool.Credentials;
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
                .url("jdbc:mysql://localhost:3306/main_dev?serverTimezone=UTC")
                .username(Credentials.USERNAME)
                .password(Credentials.PASSWORD)
                .build();
    }
}
