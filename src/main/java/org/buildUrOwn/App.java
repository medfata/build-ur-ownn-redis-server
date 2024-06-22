package org.buildUrOwn;

import org.buildUrOwn.tcpServer.RedisServer;

public class App 
{
    private static RedisServer redisServer = new RedisServer(); 
    public static void main( String[] args )
    {
        redisServer.StartServer();
    }
}
