package com.mvpjava;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheTutorial implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CacheTutorial.class);
    private final AircraftService aircraftService;
    private final CacheManager cacheManager;

    @Autowired
    public CacheTutorial(AircraftService dataService, CacheManager cacheManager) {
        this.aircraftService = dataService;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("================================================================");
        logger.info("Using cache Manager {}", cacheManager.getClass().getSimpleName());

        Aircraft cessna = new Aircraft("Cessna 650").setTopSpeed(478);
        Aircraft aerostar = new Aircraft("Aerostar PA-602P").setTopSpeed(262);
        Aircraft piper = new Aircraft("Piper PA-31-300").setTopSpeed(195);

        //store in cache upon creation
        aircraftService.createAircraft(cessna);
        aircraftService.createAircraft(aerostar);
        aircraftService.createAircraft(piper);

        logger.info("Calling getAircraftByModelName() ...");
        aircraftService.getAircraftByModelName(cessna.getModel()); //hit
        logger.info("Calling getAircraftByModelName() ...");
        aircraftService.getAircraftByModelName(aerostar.getModel());//hit
        logger.info("Calling getAircraftByModelName() ...");
        aircraftService.getAircraftByModelName(piper.getModel());//hit

        piper.setTopSpeed(200);

        aircraftService.updateAircraft(piper);//evict
        aircraftService.getAircraftByModelName(piper.getModel());//miss
        aircraftService.getAircraftByModelName(piper.getModel());//hit

        aircraftService.removeAircraft(piper);//evict
        aircraftService.getAircraftByModelName(piper.getModel());//miss

        aircraftService.clearAllCaches();//evict all caches
        aircraftService.getAircraftByModelName(cessna.getModel()); //miss
        aircraftService.getAircraftByModelName(aerostar.getModel());//miss

        logger.info(getCoffeeCacheStats().toString());
        logger.info("================================================================");
    }


    public CacheStats getCoffeeCacheStats() {
        org.springframework.cache.Cache cache = cacheManager.getCache("AIRCRAFTS");
        Cache nativeCoffeeCache = (Cache) cache.getNativeCache();
        // System.out.println(nativeCoffeeCache.asMap().toString());
        return nativeCoffeeCache.stats();
    }

}
