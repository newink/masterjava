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

    private LoadingCache<String, Integer> citiesCache;
    private CityDao cityDao;

    private CityCache() {
        cityDao = DBIProvider.getDao(CityDao.class);
        citiesCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String s) throws Exception {
                        City byMnemonic = cityDao.getByMnemonic(s);
                        return byMnemonic == null ? null : byMnemonic.getId();
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

    public Integer getCityId(String name) {
        try {
            return citiesCache.get(name);
        } catch (ExecutionException e) {
            log.error("Error while getting city ID from cache: {}", e);
            return null;
        }
    }

    public void put(String key, Integer value) {
        citiesCache.put(key, value);
    }
}
