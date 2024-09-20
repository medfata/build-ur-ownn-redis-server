package org.buildUrOwn.redisCommandHandler.impl;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;
import java.util.List;

public class DeleteCommand implements RedisCommand {
    private RedisTimestampMap redisTimestampMap;
    private RedisMap redisMap;

    public DeleteCommand(RedisMap redisMap, RedisTimestampMap redisTimestampMap){
        this.redisTimestampMap = redisTimestampMap;
        this.redisMap = redisMap;
    }

    @Override
    public String execute(String[] args) {
        GetCommand getCmd  = new GetCommand(redisMap, redisTimestampMap);
        int delCounter = 0;

        for(String key : args){
            String val = getCmd.execute(List.of(key).toArray(new String[0]));
            if(val != null && !val.isEmpty()){
                redisMap.remove(key);
                delCounter++;
            }
        }

        return String.valueOf(delCounter);
    }
}


