package com.github.fernthedev.client;

import com.github.fernthedev.exceptions.LostConnectionServer;
import com.github.fernthedev.universal.NetPlayer;

import java.util.Scanner;

public class Client {

    NetPlayer player;
    public boolean registered;
    private Scanner scanner;
    boolean running = false;
     int port;
     String host;

    public String name;
    static waitForCommand WaitForCommand;
    static Thread waitThread;

    static Thread currentThread;

    private ClientThread clientThread;



    Client(String host, int port) {
        this.port = port;
        this.host = host;
        this.scanner = Main.scanner;
        name = null;
        WaitForCommand = new waitForCommand(this);

        clientThread = new ClientThread(this);

        currentThread = new Thread(clientThread);
    }

    void initialize() {
        System.out.println("Initializing");
        name = null;
        clientThread.connected = false;
        clientThread.connectToServer = true;
        clientThread.running = true;



        if(!registered) {
            System.out.println("Type in your desired username:");
            while (!registered || name == null) {
                    name = Main.readLine("");

                    if(name == null || name.equals("")) {
                        registered = false;
                        name = null;
                    }
                    else
                    registered = true;
            }
        }

        while(!clientThread.connected && clientThread.connectToServer) {
            clientThread.connect();

        }
    }






    void throwException() {
        try {
            throw new LostConnectionServer(host);
        } catch (LostConnectionServer lostConnectionServer) {
            lostConnectionServer.printStackTrace();
        }
    }

    public ClientThread getClientThread() {
        return clientThread;
    }

    public void print(Object message) {
        System.out.println(this + " " + message);
    }
}
