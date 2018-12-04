package com.github.fernthedev.server;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner scanner;

    public static void main(String[] args) {
            scanner = new Scanner(System.in);

        if(System.console() == null) {

            String filename = Main.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(6);
            Server.getLogger().info("No console found");
            try {
                Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/c","java -jar -Xmx2G -Xms2G \"" + filename + "\""});
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

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

            if (port == -1) port = 2000;

            Server server = new Server(port);
            new Thread(server).start();
    }

    public static String readLine(String message) {
        Server.getLogger().info(message + "\n>");
        if(scanner.hasNextLine())
            return scanner.nextLine();
        else return null;
    }

    public static int readInt(String message) {
        Server.getLogger().info(message + "\n>");
        if(scanner.hasNextLine())
            return scanner.nextInt();
        else return -1;
    }
}
