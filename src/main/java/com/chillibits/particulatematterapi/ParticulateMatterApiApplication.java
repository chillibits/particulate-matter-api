/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi;

import com.chillibits.particulatematterapi.model.db.main.Client;
import com.chillibits.particulatematterapi.model.db.main.Sensor;
import com.chillibits.particulatematterapi.model.db.main.User;
import com.chillibits.particulatematterapi.model.dbold.OldSensor;
import com.chillibits.particulatematterapi.repository.ClientRepository;
import com.chillibits.particulatematterapi.repository.OldSensorRepository;
import com.chillibits.particulatematterapi.repository.SensorRepository;
import com.chillibits.particulatematterapi.repository.UserRepository;
import com.chillibits.particulatematterapi.shared.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.chillibits.particulatematterapi.repository.data")
public class ParticulateMatterApiApplication implements CommandLineRunner {

	@Autowired
	private SensorRepository sensorRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private OldSensorRepository oldSensorRepository;
	@Autowired
	private MongoTemplate template;

	public static void main(String[] args) {
		SpringApplication.run(ParticulateMatterApiApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Imports from the old api
		if(ConstantUtils.IMPORT_SENSORS_IF_TABLE_IS_EMPTY && sensorRepository.count() == 0) importFromOldSensors();

		// Rollback to timestamp
		if(ConstantUtils.ROLLBACK_TIMESTAMP > 0) rollbackToTimestamp(ConstantUtils.ROLLBACK_TIMESTAMP);

		// Create mandatory data records
		if(userRepository.count() == 0) userRepository.save(new User(ConstantUtils.UNKNOWN_USER_ID, "Unknown", "User", "info@chillibits.com", "not set", Collections.emptySet(), User.USER, User.LOCKED, System.currentTimeMillis(), System.currentTimeMillis()));
		if(clientRepository.count() == 0) clientRepository.save(new Client(ConstantUtils.UNKNOWN_CLIENT_ID, "unknownclient", "Unknown Client", "not set", Client.TYPE_NONE, "", Client.STATUS_SUPPORT_ENDED, false, 0, "0", 0, "0", "ChilliBits", ""));

		// Test space (will not be included in a stable build)

	}

	private void importFromOldSensors() {
		log.info("Importing old sensors ...");
		// Delete all contents
		sensorRepository.deleteAllInBatch();
		// Add all from the old table
		log.info("Reading old sensors ...");
		List<OldSensor> oldSensors = oldSensorRepository.findAll();
		List<Sensor> newSensors = new ArrayList<>();
		oldSensors.forEach(oldSensor -> newSensors.add(convertOldToNewSensor(oldSensor)));
		log.info("Writing new sensors ...");
		sensorRepository.saveAll(newSensors);
		log.info("Import finished.");
	}

	private void rollbackToTimestamp(long rollbackTimestamp) {
		log.info("Rolling back to " + rollbackTimestamp + " ...");
		Set<String> collectionNames = getDataCollections();
		int i = 0;
		for(String collection : collectionNames) {
			Query query = Query.query(Criteria.where("timestamp").gte(rollbackTimestamp));
			template.remove(query, collection);
			i++;
			log.info(i + "/" + collectionNames.size() + " done");
		}
		log.info("Rollback finished.");
	}

	private Sensor convertOldToNewSensor(OldSensor oldSensor) {
		return new Sensor(
				oldSensor.getChipId(),
				Collections.emptySet(),
				oldSensor.getFirmwareVersion(),
				oldSensor.getCreationDate() * 1000,
				oldSensor.getNotes(),
				oldSensor.getLastUpdate() * 1000,
				oldSensor.getLastEdit() * 1000,
				Double.parseDouble(oldSensor.getLat()),
				Double.parseDouble(oldSensor.getLng()),
				(int) (Double.parseDouble(oldSensor.getAlt()) * 100),
				oldSensor.getCountry(),
				oldSensor.getCity(),
				false,
				true,
				false
		);
	}

	private Set<String> getDataCollections() {
		Set<String> collectionNames = template.getCollectionNames();
		collectionNames.remove(ConstantUtils.LOG_TABLE_NAME);
		collectionNames.remove(ConstantUtils.STATS_TABLE_NAME);
		return collectionNames;
	}
}