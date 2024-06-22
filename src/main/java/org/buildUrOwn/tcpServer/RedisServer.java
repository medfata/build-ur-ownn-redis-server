package org.buildUrOwn.tcpServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

import org.buildUrOwn.respSerialiser.RespDeserialiser;
import org.buildUrOwn.respSerialiser.RespSerialiser;
import org.buildUrOwn.respSerialiser.impl.RespDeserializer;
import org.buildUrOwn.respSerialiser.impl.RespSerializer;

public class RedisServer{
    private static RespSerialiser respSerialiser = new RespSerializer();
    private static  RespDeserialiser respDeserialiser = new RespDeserializer();

    public static void StartServer(){
        try(ServerSocket serverSocket = new ServerSocket(6379)){
            System.out.println("Server started on port 6379");
            while(true){
                Socket clienSocket = serverSocket.accept();
                new Thread(() -> {
                    long threadId = Thread.currentThread().getId();
                    try {
                        handleClientReq(clienSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clienSocket.close();
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
                System.out.println("before reader");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = reader.readLine();
                String rawCmd = "*1\r\n$4\r\nPING\r\n";
               
        }finally{
            clientSocket.close();
        }
    }
}
