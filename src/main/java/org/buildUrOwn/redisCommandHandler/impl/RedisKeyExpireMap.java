package org.buildUrOwn.redisCommandHandler.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;

public class RedisKeyExpireMap implements RedisTimestampMap {
    private Map<String, Instant> map;
    private RedisMap redisKeyValueMap;
    public RedisKeyExpireMap(RedisMap redisKeyValueMap){
        this.map = new HashMap<>();
        this.redisKeyValueMap = redisKeyValueMap;
    }

    @Override
    public void expireKeyWithSeconds(String key,Long seconds) {
        String valOfKey = this.redisKeyValueMap.get(key);
        if(seconds == null || seconds < 0 ){
            throw new IllegalArgumentException("seconds must be a positive number!");
        }
        if(valOfKey == null){
            throw new IllegalArgumentException(key+" doesn't exist!");
        }
        Instant currentIntant = Instant.now();
        Instant futureInstant = currentIntant.plusSeconds(seconds);
        this.map.put(key, futureInstant);
    }

    @Override
    public void expireKeyWithMiliSeconds(String key,Long miliSeconds) {
        String valOfKey = this.redisKeyValueMap.get(key);
        if(miliSeconds == null || miliSeconds < 0 ){
            throw new IllegalArgumentException("miliSeconds must be a positive number!");
        }
        if(valOfKey == null){
            throw new IllegalArgumentException(key+" doesn't exist!");
        }
        Instant curreInstant = Instant.now();
        Instant futureInstant = curreInstant.plusMillis(miliSeconds);
        this.map.put(key, futureInstant);
    }
    @Override
    public Instant getTimeToExpireByKey(String key) {
        return this.map.get(key);
    }
}
