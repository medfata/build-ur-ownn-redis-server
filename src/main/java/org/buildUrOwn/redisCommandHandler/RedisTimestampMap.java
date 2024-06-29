package org.buildUrOwn.redisCommandHandler;

import java.time.Instant;

public interface RedisTimestampMap {
    //public void expireKey(String key, Date timeToExpire);
    public void expireKeyWithSeconds(String key, int seconds);
    //public void expireKeyWithMiliSeconds(String key, int miliSeconds);
    public Instant getTimeToExpireByKey(String key);
}
