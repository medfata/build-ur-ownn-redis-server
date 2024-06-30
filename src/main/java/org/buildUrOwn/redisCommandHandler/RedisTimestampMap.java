package org.buildUrOwn.redisCommandHandler;

import java.time.Instant;

public interface RedisTimestampMap {
    public void expireKeyWithSeconds(String key, Long seconds);
    public void expireKeyWithMiliSeconds(String key, Long miliSeconds);
    public Instant getTimeToExpireByKey(String key);
}
