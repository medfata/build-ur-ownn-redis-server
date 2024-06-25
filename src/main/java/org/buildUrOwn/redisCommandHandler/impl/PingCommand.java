package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;

public class PingCommand implements RedisCommand {
    
    @Override
    public String execute(String[] args) {
        return "PONG";
    }
}
