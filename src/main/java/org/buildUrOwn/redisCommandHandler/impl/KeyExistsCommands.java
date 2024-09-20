package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;

import java.time.Instant;

public class KeyExistsCommands implements RedisCommand {
    private RedisTimestampMap redisTimestampMap;

    public KeyExistsCommands(RedisTimestampMap redisTimestampMap){
        this.redisTimestampMap = redisTimestampMap;
    }
    @Override
    public Integer execute(String[] args) {
        Instant keyExpTimestamp = this.redisTimestampMap.getTimeToExpireByKey(args[0]);
        if(keyExpTimestamp == null)
            return 0;
        if(Instant.now().isAfter(keyExpTimestamp))
            return 0;
        return 1;
    }
}
