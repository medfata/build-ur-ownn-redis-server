package org.buildUrOwn;

import org.buildUrOwn.tcpServer.RedisServer;

public class App 
{
    public static void main( String[] args )
    {
        new RedisServer().startServer();
    }
}
