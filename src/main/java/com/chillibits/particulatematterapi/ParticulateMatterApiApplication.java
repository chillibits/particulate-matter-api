/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved.
 */

package com.chillibits.particulatematterapi;

import com.chillibits.particulatematterapi.repository.AuthUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = AuthUserRepository.class)
public class ParticulateMatterApiApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication((ParticulateMatterApiApplication.class));
		app.setAdditionalProfiles("dev");
		app.run(args);
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction() {
		return route(GET("/"), req ->
				ServerResponse.permanentRedirect(URI.create("swagger-ui.html")).build());
	}
}
