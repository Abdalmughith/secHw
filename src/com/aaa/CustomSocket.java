package com.aaa;

/**
 * Created by Abdalmughith Alz on 10/25/2017.
 */
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;

public class CustomSocket {
    private Socket socket;

    private DataInputStream input;

    private DataOutputStream output;

    static  byte[] skey = new byte[1000];
    static String skeyString;

    static byte[] raw;


    private static byte[] getRawKey(String secret) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(secret.getBytes());
        kgen.init(128, sr); // 128, 256 and 448 bits may not be available
        SecretKey skey = kgen.generateKey();
        raw = skey.getEncoded();
        return raw;
    }


    public CustomSocket(InetAddress acceptorHost, int acceptorPort, String secret)
            throws SocketException, IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(acceptorHost, acceptorPort), 5000);
        setStreams();
        try {
            getRawKey(secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    CustomSocket(Socket socket, String secret) throws IOException {
        this.socket = socket;
        try {
            getRawKey(secret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setStreams();
    }

    public Socket getSocket() {
        return socket;
    }

    private void setStreams() throws IOException {
        // get an input stream for reading from the data socket
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }


    public void sendMessage(String message) throws IOException {
        output.writeUTF(message);
        output.flush();
    }


    public String receiveMessage() throws IOException {
        String message = input.readUTF();
        return message;
    }


    public void close() throws IOException {
        socket.close();
    }

    public File encryptFile(File file) throws Exception {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = new byte[(int) file.length()];
        fileInputStream = new FileInputStream(file);
        fileInputStream.read(bytesArray);

        byte[] encrypt = encrypt(raw, bytesArray);

        FileOutputStream fos = new FileOutputStream("d:\\temp");
        fos.write(encrypt);
        fos.close();
        File encryptFile = new File("d:\\temp");

        return  encryptFile;
    }

    public void sendFile(File file) throws Exception {


        FileInputStream fileIn = new FileInputStream(encryptFile(file));
        byte[] buf = new byte[Short.MAX_VALUE];

        int bytesRead;
        while( (bytesRead = fileIn.read(buf)) != -1 ) {
            output.writeShort(bytesRead);
            output.write(buf,0,bytesRead);
        }
        output.writeShort(-1);
        fileIn.close();
    }
    public void decryptFile(File file) throws Exception {
        try {
            FileInputStream fileInputStream = null;
            byte[] bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);
            byte[] decrypt = decrypt(raw, bytesArray);

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(decrypt);
            fos.flush();
            fos.close();

        }
        catch(Exception e) {
            System.out.println("Error In Key!!!");
            System.out.println(e);
            e.printStackTrace();
        }

    }

    public void receiveFile(File file) throws Exception {
        FileOutputStream fileOut = new FileOutputStream(file);
        byte[] buf = new byte[Short.MAX_VALUE];
        int bytesSent;
        while( (bytesSent = input.readShort()) != -1 ) {
            input.readFully(buf,0,bytesSent);
            fileOut.write(buf,0,bytesSent);
        }

        decryptFile(file);
        fileOut.close();
    }
    public  byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public  byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "Blowfish");
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }
}