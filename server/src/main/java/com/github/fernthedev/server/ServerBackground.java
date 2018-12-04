package com.github.fernthedev.server;


import com.github.fernthedev.packets.MessagePacket;
import com.github.fernthedev.packets.PingPacket;
import com.github.fernthedev.packets.RecieveMessagePacket;
import com.github.fernthedev.universal.NetPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
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
        Server.getLogger().info("Wait for command thread created");
    }


    public void run() {
        addCommand(new ServerCommand("exit") {
            @Override
            void onCommand(String[] args) {
                Server.getLogger().info("Exiting");
                server.shutdownServer();
                System.exit(0);
            }
        }).setUsage("Safely closes the server.");

        addCommand(new ServerCommand("broadcast") {
            @Override
            void onCommand(String[] args) {
                if (args.length > 0) {
                    Server.getLogger().info("Telling all clients " + args[0]);
                    Server.sendObjectToAllPlayers(new RecieveMessagePacket(Server.serverPlayer, args[0]));
                } else {
                    Server.getLogger().info("No message?");
                }
            }
        }).setUsage("Sends a broadcast message to all clients");

        addCommand(new ServerCommand("ping") {
            @Override
            void onCommand(String[] args) {
                Server.sendObjectToAllPlayers(new PingPacket());
            }
        }).setUsage("Sends a ping packet to all clients");

        addCommand(new ServerCommand("list") {
            @Override
            void onCommand(String[] args) {
                Server.getLogger().info("Players: (" + (PlayerHandler.players.size() - 1) + ")");

                for(ClientPlayer clientPlayer : new HashMap<>(Server.clientNetPlayerList).keySet()) {
                    NetPlayer netPlayer = clientPlayer.getNetPlayer();
                    Server.getLogger().info(netPlayer.name + " :" + netPlayer.id + " { " + clientPlayer.getAdress() + "}");
                }
            }

        }).setUsage("Lists all players with ip, id and name");

        addCommand(new ServerCommand("kick") {
            @Override
            void onCommand(String[] args) {
                if(args.length == 0) {
                    Server.getLogger().info("No player to kick?");
                }else{
                    for(ClientPlayer clientPlayer : new HashMap<>(Server.clientNetPlayerList).keySet()) {
                        NetPlayer netPlayer = clientPlayer.getNetPlayer();

                        if(args[0].matches("[0-9]+")) {
                            try {
                                int id = Integer.parseInt(args[0]);
                                if (id == netPlayer.id) {
                                    if (args.length == 1) {
                                        clientPlayer.sendObject(new MessagePacket("You have been kicked."));
                                    } else {
                                        StringBuilder message = new StringBuilder();

                                        int index = 0;

                                        for (String messageCheck : args) {
                                            index++;
                                            if (index <= 1) {
                                                message.append(messageCheck);
                                            }
                                        }

                                        clientPlayer.sendObject(new MessagePacket("Kicked: " + message));
                                    }
                                    clientPlayer.close();
                                }
                            } catch (NumberFormatException e) {
                                Server.getLogger().info("Not able to parse number.");
                            }
                        }else {
                            if (netPlayer.name.equals(args[0])) {
                                if(args.length == 1) {
                                    clientPlayer.sendObject(new MessagePacket("You have been kicked."));
                                }else{
                                    StringBuilder message = new StringBuilder();

                                    int index = 0;

                                    for(String messageCheck : args){
                                        index++;
                                        if(index <= 1) {
                                            message.append(messageCheck);
                                        }
                                    }

                                    clientPlayer.sendObject(new MessagePacket("Kicked: " + message) );
                                }
                                clientPlayer.close();
                            }
                        }
                    }
                }
            }
        }).setUsage("Used to kick players using id");

        addCommand(new ServerCommand("ban") {
            @Override
            void onCommand(String[] args) {
                if(args.length <= 1) {
                    Server.getLogger().info("No player to kick or type? (ban {type} {player}) \n types: name,ip");
                }
                else{
                    String type = args[0];
                    String player = args[1];

                    for(ClientPlayer clientPlayer : new HashMap<>(Server.clientNetPlayerList).keySet()) {
                        NetPlayer netPlayer = clientPlayer.getNetPlayer();

                        if(player.matches("[0-9]+")) {
                            int id = Integer.parseInt(player);
                            if(id == netPlayer.id) {
                                StringBuilder message = new StringBuilder();

                                int index = 0;

                                for (String messageCheck : args) {
                                    index++;
                                    if (index <= 1) {
                                        message.append(messageCheck);
                                    }
                                }

                                if(type.equalsIgnoreCase("ip")) {
                                    Server.bannedIps.add(clientPlayer.getAdress());
                                }

                                if(type.equalsIgnoreCase("name")) {
                                    Server.bannedNames.add(netPlayer.name);
                                }

                                clientPlayer.sendObject(new MessagePacket("Banned: " + message));
                                clientPlayer.close();
                            }
                        }else {
                            if (netPlayer.name.equals(player)) {
                                StringBuilder message = new StringBuilder();

                                int index = 0;

                                for(String messageCheck : args){
                                    index++;
                                    if(index <= 1) {
                                        message.append(messageCheck);
                                    }
                                }

                                clientPlayer.sendObject(new MessagePacket("Kicked: " + message) );
                                clientPlayer.close();
                            }
                        }
                    }
                }
            }
        }).setUsage("Used to ban players using id. ");

        addCommand(new ServerCommand("help") {
            @Override
            void onCommand(String[] args) {
                if(args.length == 0) {
                    Server.getLogger().info("Following commands: ");
                    for(ServerCommand serverCommand : serverCommandList) {
                        Server.getLogger().info(serverCommand.getCommandName());
                    }
                }else{
                    String command = args[0];
                    boolean executed = false;

                    for (ServerCommand serverCommand : serverCommandList) {
                        if (serverCommand.getCommandName().equalsIgnoreCase(command)) {
                            if(serverCommand.getUsage().equals("")) {
                                Server.getLogger().info("No usage found.");
                            }else
                            Server.getLogger().info("Usage: \n" + serverCommand.getUsage());

                            executed = true;
                            break;
                        }
                    }
                    if(!executed) Server.getLogger().info("No such command found for help");
                }
            }
        }).setUsage("Shows list of commands or usage of a command");;

        while (server.isRunning()) {
            boolean scannerChecked = false;
            //if (scanner.hasNextLine()) {
            if (!checked) {
                Server.getLogger().info("Type Command: (try help)");
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

            command = command.replaceAll(" {2}"," ");

            if(!command.equals("")) {
                try {
                    for (ServerCommand serverCommand : serverCommandList) {
                        if (serverCommand.getCommandName().equalsIgnoreCase(command)) {
                            String[] args = new String[messageword.size()];
                            args = messageword.toArray(args);

                            Server.getLogger().info("Executing " + command);

                            new Thread(new CommandHandler(serverCommand,args)).start();

                            executed = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!executed) {
                    Server.getLogger().info("No such command found");
                }
            }
        }
    }


    private ServerCommand addCommand(@NotNull ServerCommand serverCommand) {
        serverCommandList.add(serverCommand);
        return serverCommand;
    }
}
