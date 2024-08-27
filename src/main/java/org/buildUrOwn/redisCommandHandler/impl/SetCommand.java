package org.buildUrOwn.redisCommandHandler.impl;

import java.util.Random;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;

public class SetCommand implements RedisCommand {
    public RedisMap redisMap;
    public RedisTimestampMap redisTimestampMap;
    private static final String EXPIRE_BY_SECONDS_ARG = "EX";
    private static final String EXPIRE_BY_MILISECONDS_ARG = "PX";
    private static final String EXPIRE_BY_UNIX_SECONDS_ARG = "EXAT";
    private static final String EXPIRE_BY_UNIX_MILISECONDS_ARG = "PXAT";

    public SetCommand(RedisMap redisMap, RedisTimestampMap redisTimestampMap){
        this.redisMap = redisMap;
        this.redisTimestampMap = redisTimestampMap;
    }

    @Override
    public String execute(String[] args) {
        String key = args[0];
        if ("__rand_int__".equals(key)) {
            Random random = new Random();
            int randomInt = random.nextInt(Integer.MAX_VALUE + 1);
            key = String.valueOf(randomInt);
        }
        String value = args[1];
        this.redisMap.put(key, value);
        if(args.length > 2){
            String expireTimeUnit = args[2];
            String expireTimeVal = args[3];
            switch (expireTimeUnit) {
                case EXPIRE_BY_SECONDS_ARG:
                    this.redisTimestampMap.expireKeyWithSeconds(key, Long.valueOf(expireTimeVal));
                    break;
                case EXPIRE_BY_MILISECONDS_ARG:
                    this.redisTimestampMap.expireKeyWithMiliSeconds(key, Long.valueOf(expireTimeVal));
                    break;
                case EXPIRE_BY_UNIX_SECONDS_ARG:
                    this.redisTimestampMap.expireKeyWithUnixTime(key, Long.valueOf(expireTimeVal), "seconds");
                    break;
                case EXPIRE_BY_UNIX_MILISECONDS_ARG:
                    this.redisTimestampMap.expireKeyWithUnixTime(key, Long.valueOf(expireTimeVal), "miliseconds");
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return "OK";
    }
}
