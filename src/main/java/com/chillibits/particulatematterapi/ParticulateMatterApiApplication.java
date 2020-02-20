/*
 * Copyright © 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParticulateMatterApiApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication((ParticulateMatterApiApplication.class));
		app.setAdditionalProfiles("dev");
		app.run(args);
	}
}
