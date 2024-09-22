package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;

public class DecrementCommand implements RedisCommand {
    private final RedisMap redisMap;
    private final RedisTimestampMap redisTimestampMap;
    public DecrementCommand(RedisMap redisMap, RedisTimestampMap redisTimestampMap){
        this.redisMap = redisMap;
        this.redisTimestampMap = redisTimestampMap;
    }

    @Override
    public Object execute(String[] args) {
        GetCommand getCmd = new GetCommand(redisMap, redisTimestampMap);
        String res = getCmd.execute(args);
        if(res == null || res.isEmpty())
            this.redisMap.put(args[0], "0");
        var val = this.redisMap.get(args[0]);
        try{
            int intVal = Integer.parseInt(val);
            this.redisMap.put(args[0], String.valueOf(--intVal));
            return this.redisMap.get(args[0]);
        }catch(Exception ex){
            return "-Error value in "+args[0]+" is not a valid integer";
        }
    }
}
