package org.buildUrOwn.redisCommandHandler;

public interface RedisMap {
    public void put(String key, String value);
    public String get(String key);
    public String remove(String key);
    public boolean contains(String key);
}
