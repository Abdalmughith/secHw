package com.aaa;

/**
 * Created by Abdalmughith Alz on 10/25/2017.
 */

import java.io.*;
import java.net.InetAddress;


public class Client {
    public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);

        try {
            String hostName =  "localhost";
            String portNum = "7";
            CustomSocket socket = new CustomSocket(InetAddress.getByName(hostName), Integer.parseInt(portNum), "AAAAA");
            String echo;
            while (true) {
                socket.sendMessage("200");
                System.out.println("Enter file location.");
                String path = br.readLine();
                File file = null;
                try {
                    file = new File(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                socket.sendMessage(file.getName());
                socket.sendFile(file);

                echo = socket.receiveMessage();
                System.out.println(echo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}