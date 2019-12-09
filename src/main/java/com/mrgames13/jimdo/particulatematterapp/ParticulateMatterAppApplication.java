/*
 * Copyright © 2019 Marc Auberer. All rights reserved.
 */

package com.mrgames13.jimdo.particulatematterapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParticulateMatterAppApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication((ParticulateMatterAppApplication.class));
		app.setAdditionalProfiles("dev");
		app.run(args);
	}
}