package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;

public class GetCommand implements RedisCommand {
    public RedisMap redisMap;

    public GetCommand(RedisMap redisMap){
        this.redisMap = redisMap;
    }

    @Override
    public String execute(String[] args) {
        String val = this.redisMap.get(args[0]);
        if(val != null){
            return '"'+val+'"';
        }
        return "";
    }
}
