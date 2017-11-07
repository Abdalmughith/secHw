package com.aaa;

/**
 * Created by Abdalmughith Alz on 10/25/2017.
 */

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {

        int serverPort = 7;
        String message;
        if (args.length == 1) serverPort = Integer.parseInt(args[0]);
        try {
            ServerSocket myConnectionSocket = new ServerSocket(serverPort);
            while (true) {
                System.out.println("Waiting for a connection.");
                CustomSocket myDataSocket = new CustomSocket(myConnectionSocket.accept(), "AAAAA");
                System.out.println("connected");
                while (true) {
                    message = myDataSocket.receiveMessage();
                    if ((message.trim()).equals("200")) {
                        String fileName = myDataSocket.receiveMessage();
                        System.out.println(fileName);
                        File outFile = new File("e:\\output\\" + fileName);
                        myDataSocket.receiveFile(outFile);
                        myDataSocket.sendMessage("File received " + outFile.length() + " bytes");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}