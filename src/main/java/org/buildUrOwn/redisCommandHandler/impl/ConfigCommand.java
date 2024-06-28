package org.buildUrOwn.redisCommandHandler.impl;

import java.util.ArrayList;
import java.util.List;

import org.buildUrOwn.redisCommandHandler.RedisCommand;

public class ConfigCommand implements RedisCommand {
    @Override
    public Object execute(String[] args) {
        List<String> resp = new ArrayList<>();
        resp.add(args[0]);
        switch (args[0]) {
            case "save":
                resp.add("900 1 300 10");
                break;
            case "appendonly":
                resp.add("yse");
            default:
                break;
        }
        String[] stockArr = new String[resp.size()];
        return resp.toArray(stockArr);
    }
}
