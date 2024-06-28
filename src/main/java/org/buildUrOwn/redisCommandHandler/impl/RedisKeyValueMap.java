package org.buildUrOwn.redisCommandHandler.impl;

import java.util.HashMap;
import java.util.Map;
import org.buildUrOwn.redisCommandHandler.RedisMap;

public class RedisKeyValueMap implements RedisMap {

    private Map<String, String> map;

    public RedisKeyValueMap() {
        this.map = new HashMap<>();
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public String remove(String key) {
        return map.remove(key);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public String get(String key) {
        return map.get(key);
    }
}
