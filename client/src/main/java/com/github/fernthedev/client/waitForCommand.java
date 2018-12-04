package com.github.fernthedev.client;

import com.github.fernthedev.packets.SendMessagePacket;

import java.util.Scanner;

public class waitForCommand implements Runnable {

    static boolean running;

    private Scanner scanner;
    private Client client;
    private boolean checked;

    waitForCommand(Client client) {
        running = false;
        this.client = client;
        this.scanner = Main.scanner;
        checked = false;
    }


    public void run() {
        running = true;
       // client.getLogger().info("Starting the runnable for wait for command ;) " + client.running );
        while (client.running) {
          //  if (client.registered) {

                    //if (scanner.hasNextLine()) {
                    if (!checked) {
                        client.getLogger().info("Type Command:\n>");
                        checked = true;
                    }
                String sendmessage = scanner.nextLine();
                //client.getLogger().info("the message" + sendmessage);
                client.getClientThread().sendObject(new SendMessagePacket(client.player, sendmessage));
                // }
         //   }
        }
    }
}
