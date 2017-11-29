package ru.javaops.masterjava.upload;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.model.City;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CityCache {
    private static CityCache instance;

    private LoadingCache<String, City> citiesCache;
    private CityDao cityDao;

    private CityCache() {
        cityDao = DBIProvider.getDao(CityDao.class);
        citiesCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, City>() {
                    @Override
                    public City load(String s) throws Exception {
                        return cityDao.getByMnemonic(s);
                    }
                });
    }

    public static CityCache getInstance() {
        if (instance == null) {
            synchronized (CityCache.class) {
                if (instance == null) {
                    instance = new CityCache();
                }
            }
        }
        return instance;
    }

    public City getCityId(String name) {
        try {
            return citiesCache.get(name);
        } catch (ExecutionException e) {
            log.error("Error while getting city ID from cache: {}", e);
            return null;
        }
    }

    public void put(String key, City value) {
        citiesCache.put(key, value);
    }
}
