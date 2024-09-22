package org.buildUrOwn.tcpServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;
import org.buildUrOwn.redisCommandHandler.impl.*;
import org.buildUrOwn.respSerialiser.RespDeserialiser;
import org.buildUrOwn.respSerialiser.RespSerialiser;
import org.buildUrOwn.respSerialiser.impl.RespDeserializer;
import org.buildUrOwn.respSerialiser.impl.RespSerializer;

public class RedisServer{
    private static final RespSerialiser respSerialiser = new RespSerializer();
    private static final RespDeserialiser respDeserialiser = new RespDeserializer();
    private static final RedisMap redisMap = new RedisKeyValueMap();
    private static final RedisTimestampMap redisTimestampMap = new RedisKeyExpireMap(redisMap);
    private static final Map<String, RedisCommand> redisCommandMap;
    static {
        redisCommandMap = new HashMap<>();
        redisCommandMap.put("PING", new PingCommand());
        redisCommandMap.put("ECHO", new EchoCommand());
        redisCommandMap.put("SET", new SetCommand(redisMap, redisTimestampMap));
        redisCommandMap.put("GET", new GetCommand(redisMap, redisTimestampMap));
        redisCommandMap.put("CONFIG", new ConfigCommand());
        redisCommandMap.put("EXISTS", new KeyExistsCommands(redisTimestampMap));
        redisCommandMap.put("DEL", new DeleteCommand(redisMap, redisTimestampMap));
        redisCommandMap.put("INCR", new IncrementCommand(redisMap, redisTimestampMap));
        redisCommandMap.put("DECR", new DecrementCommand(redisMap, redisTimestampMap));
    }
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);

        // Register the new client connection for reading
        clientChannel.register(key.selector(), SelectionKey.OP_READ);
        System.out.println("Accepted connection from: " + clientChannel.getRemoteAddress());
    }
    private void handleClientReqV2(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            // Close the connection if the client has disconnected
            clientChannel.close();
            System.out.println("Connection closed by client");
        } else {
            buffer.flip();
            // Process client request here
            StringBuilder rawCmdBuilder = new StringBuilder();
            String clientMessage = new String(buffer.array(), 0, bytesRead);
            rawCmdBuilder.append(clientMessage).append("\r\n");
            String rawCmd = rawCmdBuilder.toString();
            String[] rawSubCmds = rawCmd.split("(\\*)");
            for(String subCmd : rawSubCmds){
                if(subCmd.isEmpty())
                    continue;
                subCmd = "*"+subCmd;
                String cmdToPrint = subCmd.replace("\r\n", " ");
                System.out.println("raw cmd: "+cmdToPrint);
                Object  deserializedCmd = respDeserialiser.deserialise(subCmd);
                String[] deserializedCmdParts = ((String[]) deserializedCmd);
                String deserializedCmdStr = deserializedCmdParts[0];
                RedisCommand redisCommandHandler = handleRedisCommand(deserializedCmdStr);
                String[] commandArgs = Arrays.copyOfRange(deserializedCmdParts, 1, deserializedCmdParts.length);
                Object output = redisCommandHandler != null ?  redisCommandHandler.execute(commandArgs) : "-Error invalid redis command";
                // Sending back a response to the client
                ByteBuffer responseBuffer = ByteBuffer.wrap(respSerialiser.serialise(output).getBytes());
                clientChannel.write(responseBuffer);
            }
            buffer.clear();
        }
    }

    public void startServer() {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            // Configure the server socket channel to be non-blocking
            serverSocketChannel.bind(new InetSocketAddress(6379));
            serverSocketChannel.configureBlocking(false);

            // Register the server socket channel with the selector for accept events
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server started on port 6379");

            while (true) {
                // Wait for an event
                selector.select();

                // Get the selection keys and iterate over them
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        // Accept a new client connection
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        // Read data from an existing connection
                        handleClientReqV2(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static RedisCommand handleRedisCommand(String command){
        return redisCommandMap.get(command);
    }
    
}
