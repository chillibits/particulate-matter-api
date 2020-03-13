package com.chillibits.particulatematterapi.repository.data;

import com.chillibits.particulatematterapi.model.db.DataRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataRepository extends MongoRepository<DataRecord, String> {

}