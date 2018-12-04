package com.github.fernthedev.client;

import com.github.fernthedev.universal.StaticHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner;

    static Client client;

    public static void main(String[] args) {
        //new StaticHandler();
        Logger.getLogger("io.netty").setLevel(Level.OFF);
        scanner = new Scanner(System.in);

        String host = null;
        int port = -1;

        for(int i = 0;i < args.length;i++) {
            String arg = args[i];

            if(arg.equalsIgnoreCase("-port")) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    port = -1;
                }
            }

            if(arg.equalsIgnoreCase("-ip")) {
                try {
                    host = args[i + 1];
                } catch (IndexOutOfBoundsException e) {
                    host = null;
                }
            }

            if(arg.equalsIgnoreCase("-debug")) {
                StaticHandler.isDebug = true;
            }
        }

        if(System.console() == null && !StaticHandler.isDebug) {

            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Client.getLogger().info("No console found");

            String[] newArgs = new String[]{"cmd","/c","start","cmd","/c","java -jar -Xmx2G -Xms2G \"" + filename + "\""};

            List<String> launchArgs = new ArrayList<>(Arrays.asList(newArgs));
            launchArgs.addAll(Arrays.asList(args));

            try {
                Runtime.getRuntime().exec(launchArgs.toArray(new String[]{}));
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(StaticHandler.isDebug) Client.getLogger().setLevel(Level.DEBUG);
        else Client.getLogger().setLevel(Level.INFO);

        while(host == null || port == -1) {
            if(host == null)
            host= readLine("Host:");

            if(port == -1)
                port = readInt("Port:");
        }

        client = new Client(host,port);
        Client.getLogger().isDebugEnabled();
        client.initialize();
    }

    public static String readLine(String message) {
        if(!(message == null || message.equals(""))) {
            Client.getLogger().info(message);
        }
        if(scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        else return null;
    }

    public static int readInt(String message) {
        if(!(message == null || message.equals(""))) {
            Client.getLogger().info(message);
        }
        if(scanner.hasNextLine()) {
            return scanner.nextInt();
        }
        else return -1;
    }

}
