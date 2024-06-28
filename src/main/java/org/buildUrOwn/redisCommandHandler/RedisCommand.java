package org.buildUrOwn.redisCommandHandler;

public interface RedisCommand {
    Object execute(String[] args);
}
