package org.buildUrOwn.redisCommandHandler.impl;

import java.time.Instant;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;

public class GetCommand implements RedisCommand {
    private RedisMap redisMap;
    private RedisTimestampMap redisTimestampMap;

    public GetCommand(RedisMap redisMap, RedisTimestampMap redisTimestampMap){
        this.redisMap = redisMap;
        this.redisTimestampMap = redisTimestampMap;
    }

    @Override
    public String execute(String[] args) {
        Instant keyExpTimestamp = this.redisTimestampMap.getTimeToExpireByKey(args[0]);
        if(keyExpTimestamp != null && Instant.now().isAfter(keyExpTimestamp))
            return "";
        String val = this.redisMap.get(args[0]);
        if(val != null){
            return '"'+val+'"';
        }
        return "";
    }
}
