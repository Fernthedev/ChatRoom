package com.github.fernthedev.server;


import com.github.fernthedev.packets.PingPacket;
import com.github.fernthedev.packets.RecieveMessagePacket;

import java.util.Scanner;
public class ServerBackground implements Runnable {

    private Server server;
    private Scanner scanner;

    public Scanner getScanner() {
        return scanner;
    }

    private boolean checked;

    ServerBackground(Server server) {
        this.server = server;
        this.scanner = Main.scanner;
        checked = false;
        System.out.println("Wait for command thread created");
    }


    public void run() {

        while (server.isRunning()) {
            boolean scannerChecked = false;
                //if (scanner.hasNextLine()) {
                if (!checked) {
                    System.out.println("Type Command:");
                    checked = true;
                }
                String command = scanner.nextLine();
                System.out.println("Executing " + command);
                String[] checkmessage = command.split(" ",2);
                String messageword = null;



                if (checkmessage.length > 1) {
                    messageword = command.split(" ", 2)[1];
                }

                command = checkmessage[0];

                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting");
                    server.shutdownServer();
                    System.exit(0);

                        /*
                        for (Thread thread : Thread.getAllStackTraces().keySet()) {
                            try {
                                //server. ();
                                thread.join();
                                System.exit(0);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }*/


                    //Main.server.sendObject(new SendMessagePacket(Main.client.player, sendmessage));
                } else if (command.equalsIgnoreCase("broadcast")) {
                    if(messageword == null) {
                        System.out.println("No message?");
                    }else {
                        System.out.println("Telling all clients " + messageword);
                        Server.sendObjectToAllPlayers(new RecieveMessagePacket(Server.serverPlayer, messageword));
                    }


                } else if(command.equalsIgnoreCase("ping")) {
                    Server.sendObjectToAllPlayers(new PingPacket());
                }/* else {
                    System.out.println("No scanner ;(");
                    scannerChecked = true;
                }*/
                //}else {
                //      System.out.println("No running");
                //   }
        }
    }
}
