package com.chillibits.particulatematterapi.config.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.chillibits.particulatematterapi.repository.main", mongoTemplateRef = "secondaryMongoTemplate")
public class SecondaryMongoConfig {}