package com.github.fernthedev.server;


import com.github.fernthedev.packets.PingPacket;
import com.github.fernthedev.packets.RecieveMessagePacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ServerBackground implements Runnable {

    private Server server;
    private Scanner scanner;

    List<ServerCommand> serverCommandList = new ArrayList<>();


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
        addCommand(new ServerCommand("exit") {
            @Override
            void onCommand(String[] args) {
                System.out.println("Exiting");
                server.shutdownServer();
                System.exit(0);
            }
        }).setUsage("Safely closes the server.");

        addCommand(new ServerCommand("broadcast") {
            @Override
            void onCommand(String[] args) {
                if (args.length > 0) {
                    System.out.println("Telling all clients " + args[0]);
                    Server.sendObjectToAllPlayers(new RecieveMessagePacket(Server.serverPlayer, args[0]));
                } else {
                    System.out.println("No message?");
                }
            }
        }).setUsage("Sends a broadcast message to all clients");

        addCommand(new ServerCommand("ping") {
            @Override
            void onCommand(String[] args) {
                Server.sendObjectToAllPlayers(new PingPacket());
            }
        }).setUsage("Sends a ping packet to all clients");

        addCommand(new ServerCommand("help") {
            @Override
            void onCommand(String[] args) {
                if(args.length == 0) {
                    System.out.println("Following commands: ");
                    for(ServerCommand serverCommand : serverCommandList) {
                        System.out.println(serverCommand.getCommandName());
                    }
                }else{
                    String command = args[0];
                    boolean executed = false;

                    for (ServerCommand serverCommand : serverCommandList) {
                        if (serverCommand.getCommandName().equalsIgnoreCase(command)) {
                            if(serverCommand.getUsage().equals("")) {
                                System.out.println("No usage found.");
                            }else
                            System.out.println("Usage: \n" + serverCommand.getUsage());

                            executed = true;
                            break;
                        }
                    }
                    if(!executed) System.out.println("No such command found for help");
                }
            }
        }).setUsage("Shows list of commands or usage of a command");;

        while (server.isRunning()) {
            boolean scannerChecked = false;
            //if (scanner.hasNextLine()) {
            if (!checked) {
                System.out.println("Type Command: (try help)");
                checked = true;
            }
            String command = scanner.nextLine();

            String[] checkmessage = command.split(" ", 2);
            List<String> messageword = new ArrayList<>();


            if (checkmessage.length > 1) {
                String [] messagewordCheck = command.split(" ");

                int index = 0;

                for(String message : messagewordCheck) {
                    index++;
                    if(index == 1 || message == null || message.equals("") || message.equals(" ")) continue;


                    messageword.add(message);
                }


            }

            command = checkmessage[0];

            boolean executed = false;

            try {
                for (ServerCommand serverCommand : serverCommandList) {
                    if (serverCommand.getCommandName().equalsIgnoreCase(command)) {
                        String[] args = new String[messageword.size()];
                        args = messageword.toArray(args);

                        System.out.println("Executing " + command);
                        serverCommand.onCommand(args);
                        executed = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!executed) {
                System.out.println("No such command found");
            }
        }
    }


    private ServerCommand addCommand(@NotNull ServerCommand serverCommand) {
        serverCommandList.add(serverCommand);
        return serverCommand;
    }
}
