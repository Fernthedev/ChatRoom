package com.github.fernthedev.client;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner scanner;

    static Client client;

    public static void main(String[] args) {

        if(System.console() == null) {

            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            System.out.println("No console found");
            try {
                Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/c","java -jar -Xmx2G -Xms2G \"" + filename + "\""});
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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
        }

        while(host == null || port == -1) {
            if(host == null)
            host= readLine("Host:");

            if(port == -1)
                port = readInt("Port:");
        }

        client = new Client(host,port);
        client.initialize();
    }

    public static String readLine(String message) {
        if(!(message == null || message.equals(""))) {
            System.out.println(message);
        }
        if(scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        else return null;
    }

    public static int readInt(String message) {
        if(!(message == null || message.equals(""))) {
            System.out.println(message);
        }
        if(scanner.hasNextLine()) {
            return scanner.nextInt();
        }
        else return -1;
    }

}
