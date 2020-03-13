package com.chillibits.particulatematterapi.repository.main;

import com.chillibits.particulatematterapi.model.db.Sensor;
import com.chillibits.particulatematterapi.model.io.RankingItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SensorRepository extends MongoRepository<Sensor, Long> {
    //Integer updateSensor(int chipId, double latitude, double longitude, double lastValueP1, double lastValueP2);
    List<RankingItem> getRankingByCity(int items);
    List<RankingItem> getRankingByCountry(int items);
}