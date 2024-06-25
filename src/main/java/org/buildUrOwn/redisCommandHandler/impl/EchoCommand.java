package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;

public class EchoCommand implements RedisCommand {
    @Override
    public String execute(String[] args) {
        return args[0];
    }
}
