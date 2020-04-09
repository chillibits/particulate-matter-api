/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@Configuration
@DependsOn("mongoTemplate")
@Slf4j
public class CronJobs {

    @Autowired
    private MongoTemplate mongoTemplate;

    // ------------------------------------------------- Indexing service ----------------------------------------------

    @Scheduled(cron = "0 0 5 * * ?") // Every day at 5:00
    public void initIndexes() {
        log.info("Start indexing ...");
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        int i = 1;
        for(String collectionName : collectionNames) {
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("timestamp", Sort.Direction.ASC));
            log.info(String.valueOf(i++));
        }
        log.info("Finished indexing.");
    }


}