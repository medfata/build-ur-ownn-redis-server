package org.buildUrOwn.redisCommandHandler;

import java.time.Instant;

public interface RedisTimestampMap {
    public void expireKeyWithSeconds(String key, Long seconds);
    public void expireKeyWithMiliSeconds(String key, Long miliSeconds);
    public void expireKeyWithUnixTime(String key, Long unixTimeSec, String unixTimeUnit);
    public Instant getTimeToExpireByKey(String key);
    public void removeKey(String key);
}
