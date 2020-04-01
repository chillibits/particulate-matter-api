/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi;

import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.dbold.OldSensor;
import com.chillibits.particulatematterapi.repository.OldSensorRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.chillibits.particulatematterapi.repository.data")
public class ParticulateMatterApiApplication implements CommandLineRunner {

	@Autowired
	private SensorRepository sensorRepository;
	@Autowired
	private OldSensorRepository oldSensorRepository;
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(ParticulateMatterApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Imports from the old api
		//importFromOldSensors();

		// Test space (will not be included in a stable build)

	}

	private void importFromOldSensors() throws Exception {
		// Delete all contents
		sensorRepository.deleteAllInBatch();
		// Add all from the old table
		List<OldSensor> oldSensors = oldSensorRepository.findAll();
		List<Sensor> newSensors = new ArrayList<>();
		oldSensors.forEach(oldSensor -> newSensors.add(convertOldToNewSensor(oldSensor)));
		sensorRepository.saveAll(newSensors);
	}

	private Sensor convertOldToNewSensor(OldSensor oldSensor) {
		return new Sensor(
				oldSensor.getChipId(),
				Collections.emptySet(),
				oldSensor.getFirmwareVersion(),
				oldSensor.getCreationDate(),
				oldSensor.getNotes(),
				oldSensor.getLastUpdate(),
				oldSensor.getLastEdit(),
				Double.parseDouble(oldSensor.getLat()),
				Double.parseDouble(oldSensor.getLng()),
				Double.parseDouble(oldSensor.getAlt()),
				oldSensor.getCountry(),
				oldSensor.getCity()
		);
	}
}