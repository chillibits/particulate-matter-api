/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.chillibits.particulatematterapi.repository.data")
public class ParticulateMatterApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ParticulateMatterApiApplication.class, args);
	}

	public void populateSensorsDatabase() {

	}
}