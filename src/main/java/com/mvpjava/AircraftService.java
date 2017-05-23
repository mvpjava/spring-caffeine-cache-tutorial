package com.mvpjava;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = {"AIRCRAFTS"})
public class AircraftService {

    private static final Logger logger = LoggerFactory.getLogger(AircraftService.class);
    private final MongoTemplate template;
    private final String mongoCollection = "aircraft";

    @Autowired
    public AircraftService(MongoTemplate template) {
        this.template = template;
    }

    @Cacheable(unless = "#result == null")
    public Aircraft getAircraftByModelName(String modelName) {
        logger.info("Executing getAircraftByModelName for model:{} \tCache MISS!", modelName);
        slowDownForDemo();
        Query query = new Query(Criteria.where("model").is(modelName));
        return template.findOne(query, Aircraft.class, mongoCollection);
    }

    @CachePut(key = "#aircraft.model", condition = "#aircraft.topSpeed > 0", unless = "#result == null")
    public Aircraft createAircraft(Aircraft aircraft) {
        logger.info("Executing createAircraft, model:{}", aircraft.getModel());
        try {
            template.insert(aircraft, mongoCollection);
        } catch (DuplicateKeyException dke) {
            //OK for demo
        }
        return template.findOne(
                new Query(Criteria.where("model").is(aircraft.getModel())),
                Aircraft.class, mongoCollection);//has id
    }

    @CacheEvict(key = "#aircraft.model")
    public void updateAircraft(Aircraft aircraft) {
        logger.info("Executing updateAircraft, model:{} topSpeed: {} Cache Evict!",
                aircraft.getModel(), aircraft.getTopSpeed());
        try {
            template.save(aircraft, mongoCollection);
        } catch (DuplicateKeyException dke) {
            //OK for demo
        }
    }

    @CacheEvict(key = "#aircraft.model")
    public void removeAircraft(Aircraft aircraft) {
        logger.info("Executing removeAircraft, model:{} \tCache Evict!", aircraft.getModel());
        template.remove(aircraft, mongoCollection);
    }

    @Caching(evict = {
        @CacheEvict(value = "AIRCRAFTS", allEntries = true),
        @CacheEvict(value = "SECOND_CACHE", allEntries = true)
    })
    public void clearAllCaches() {
        logger.info("Cleared all caches");
    }


    private void slowDownForDemo() {
        try {
            Thread.sleep(2000L);
        } catch (Exception e) {
        }
    }

}
