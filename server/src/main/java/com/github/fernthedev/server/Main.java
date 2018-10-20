package com.github.fernthedev.server;

import java.util.Scanner;

public class Main {

    static Scanner scanner;

    public static void main(String[] args) {
            scanner = new Scanner(System.in);

            int port = -1;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];

                if (arg.equalsIgnoreCase("-port")) {
                    try {
                        port = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        port = -1;
                    }
                }
            }

            if (port == -1) port = 25560;

            Server server = new Server(port);
            server.startServer();
    }

    public static String readLine(String message) {
        System.out.println(message + "\n>");
        if(scanner.hasNextLine())
            return scanner.nextLine();
        else return null;
    }

    public static int readInt(String message) {
        System.out.println(message + "\n>");
        if(scanner.hasNextLine())
            return scanner.nextInt();
        else return -1;
    }
}
