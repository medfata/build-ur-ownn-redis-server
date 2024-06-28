package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;

public class SetCommand implements RedisCommand {
    public RedisMap redisMap;

    public SetCommand(RedisMap redisMap){
        this.redisMap = redisMap;
    }

    @Override
    public String execute(String[] args) {
        this.redisMap.put(args[0], args[1]);
        return "OK";
    }
}
