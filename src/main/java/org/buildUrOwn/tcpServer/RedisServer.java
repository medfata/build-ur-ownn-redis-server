package org.buildUrOwn.tcpServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import org.buildUrOwn.redisCommandHandler.RedisCommand;
import org.buildUrOwn.redisCommandHandler.RedisMap;
import org.buildUrOwn.redisCommandHandler.RedisTimestampMap;
import org.buildUrOwn.redisCommandHandler.impl.ConfigCommand;
import org.buildUrOwn.redisCommandHandler.impl.EchoCommand;
import org.buildUrOwn.redisCommandHandler.impl.GetCommand;
import org.buildUrOwn.redisCommandHandler.impl.PingCommand;
import org.buildUrOwn.redisCommandHandler.impl.RedisKeyExpireMap;
import org.buildUrOwn.redisCommandHandler.impl.RedisKeyValueMap;
import org.buildUrOwn.redisCommandHandler.impl.SetCommand;
import org.buildUrOwn.respSerialiser.RespDeserialiser;
import org.buildUrOwn.respSerialiser.RespSerialiser;
import org.buildUrOwn.respSerialiser.impl.RespDeserializer;
import org.buildUrOwn.respSerialiser.impl.RespSerializer;

public class RedisServer{
    private static final RespSerialiser respSerialiser = new RespSerializer();
    private static final RespDeserialiser respDeserialiser = new RespDeserializer();
    private static final RedisMap redisMap = new RedisKeyValueMap();
    private static final RedisTimestampMap redisTimestampMap = new RedisKeyExpireMap(redisMap);
    public void StartServer(){
        try(ServerSocket serverSocket = new ServerSocket(6379)){
            System.out.println("Server started on port 6379");
            while(true){
                Socket clientSocket = serverSocket.accept();
                clientSocket.setSoTimeout(1);
                new Thread(() -> {
                    long threadId = Thread.currentThread().getId();
                    try {
                        handleClientReq(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Thread Id "+threadId+" finished processig");
                    }
                }).start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private static void handleClientReq(Socket clientSocket) throws IOException{
        try(BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder rawCmdBuilder = new StringBuilder();
                String line;
                try{
                    while ((line = reader.readLine()) != null) {
                        long threadId = Thread.currentThread().getId();
                        rawCmdBuilder.append(line).append("\r\n");
                    }
                }catch(SocketTimeoutException e){
                    System.out.println("socket timed out Exception!");
                }
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
                    out.write(respSerialiser.serialise(output).getBytes());
                }
                out.flush();
        }finally{
            clientSocket.close();
        }
    }

    private static RedisCommand handleRedisCommand(String command){
        if(command.equals("PING")){
            return new PingCommand();
        }else if(command.equals("ECHO")){
            return new EchoCommand();
        }else if(command.equalsIgnoreCase("set")){
            return new SetCommand(redisMap, redisTimestampMap);
        }else if(command.equalsIgnoreCase("get")){
            return new GetCommand(redisMap, redisTimestampMap);
        }else if(command.endsWith("CONFIG")){
            return new ConfigCommand();
        }
        return null;
    }
    
}
