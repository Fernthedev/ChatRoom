package com.github.fernthedev.server;


import com.github.fernthedev.exceptions.DebugException;
import com.github.fernthedev.packets.message.MessagePacket;
import com.github.fernthedev.packets.message.RecieveMessagePacket;
import com.github.fernthedev.universal.NetPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.github.fernthedev.server.CommandHandler.clientCommandList;
import static com.github.fernthedev.server.CommandHandler.serverCommandList;

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
        Server.getLogger().info("Wait for command thread created");
    }


    public void run() {
        registerCommands();

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
                    if(message == null) continue;

                    message = message.replaceAll(" {2}"," ");

                    index++;
                    if(index == 1 || message.equals("")) continue;




                    messageword.add(message);
                }
            }

            command = checkmessage[0];

            boolean executed = false;

            command = command.replaceAll(" {2}"," ");

            if(!command.equals("")) {
                try {
                    for (Command serverCommand : serverCommandList) {
                        if (serverCommand.getCommandName().equalsIgnoreCase(command)) {
                            String[] args = new String[messageword.size()];
                            args = messageword.toArray(args);

                           // Server.getLogger().info("Executing " + command);

                            new Thread(new CommandHandler(server.getConsole(),serverCommand,args)).start();

                            executed = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    Server.getLogger().error(e.getMessage(),e.getCause());
                }

                if (!executed) {
                    Server.getLogger().info("No such command found");
                }
            }
        }
    }


    private void registerCommands() {
        addServerCommand(new Command("exit") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
                Server.getLogger().info("Exiting");
                server.shutdownServer();
                System.exit(0);
            }
        }).setUsage("Safely closes the server.");

        addServerCommand(new Command("broadcast") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
                if (args.length > 0) {
                    StringBuilder argString = new StringBuilder();

                    int index = 0;

                    for(String arg : args) {
                        index++;

                        if(index == 1) {
                            argString.append(arg);
                        }else {
                            argString.append(" ");
                            argString.append(arg);
                        }
                    }

                    String message = argString.toString();

                    Server.getLogger().info(Server.serverPlayer.name + ":" + message);
                    Server.sendObjectToAllPlayers(new RecieveMessagePacket(Server.serverPlayer,message));
                } else {
                    Server.getLogger().info("No message?");
                }
            }
        }).setUsage("Sends a broadcast message to all clients");

        addServerCommand(new Command("ping") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
               ClientPlayer.pingAll();
            }
        }).setUsage("Sends a ping packet to all clients");

        addServerCommand(new Command("list") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
                Server.getLogger().info("Players: (" + (PlayerHandler.players.size() - 1) + ")");

                for(ClientPlayer clientPlayer : new HashMap<>(Server.clientNetPlayerList).keySet()) {
                    NetPlayer netPlayer = clientPlayer.getNetPlayer();
                    Server.getLogger().info(netPlayer.name + " :" + netPlayer.id + " { " + clientPlayer.getAdress() + "} Ping:" + netPlayer.ping + "ms");
                }
            }

        }).setUsage("Lists all players with ip, id and name");

        addServerCommand(new Command("kick") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
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

        addServerCommand(new Command("ban") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
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

        addServerCommand(new Command("help") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
                if(args.length == 0) {
                    Server.getLogger().info("Following commands: ");
                    for(Command serverCommand : serverCommandList) {
                        Server.getLogger().info(serverCommand.getCommandName());
                    }
                }else{
                    String command = args[0];
                    boolean executed = false;

                    for (Command serverCommand : serverCommandList) {
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
        }).setUsage("Shows list of commands or usage of a command");

        addClientCommand(new Command("ping") {
            @Override
            void onCommand(CommandSender sender,String[] args) {
                try {
                    throw new DebugException();
                } catch (DebugException e) {
                    Server.getLogger().error(e.getMessage(),e.getCause());
                }
                if(sender instanceof ClientPlayer) {
                    ClientPlayer clientPlayer = (ClientPlayer) sender;
                    clientPlayer.ping();
                }
            }
        });

        addClientCommand(new Command("list") {
            @Override
            void onCommand(CommandSender sender, String[] args) {
                sender.sendMessage("Players: (" + (PlayerHandler.players.size() - 1) + ")");

                for(ClientPlayer clientPlayer : new HashMap<>(Server.clientNetPlayerList).keySet()) {
                    NetPlayer netPlayer = clientPlayer.getNetPlayer();
                    if(netPlayer == null) continue;

                    sender.sendMessage(netPlayer.name + " :" + netPlayer.id + " Ping:" + netPlayer.ping + "ms");
                }
            }
        });

        addClientCommand(new Command("help") {
            @Override
            void onCommand(CommandSender sender, String[] args) {
                if(args.length == 0 || args[0].equals("")) {
                    for(Command serverCommand : clientCommandList) {
                        sender.sendPacket(new MessagePacket(serverCommand.getCommandName()));
                    }
                }else{
                    String command = args[0];
                    boolean executed = false;

                    for (Command serverCommand : clientCommandList) {
                        if (serverCommand.getCommandName().equalsIgnoreCase(command)) {
                            if(serverCommand.getUsage().equals("")) {
                                sender.sendMessage("No usage found.");
                            }else
                                sender.sendMessage("Usage: \n" + serverCommand.getUsage());

                            executed = true;
                            break;
                        }
                    }
                    if(!executed) sender.sendMessage("No such command found for help");
                }
            }
        });
    }

    private Command addServerCommand(@NotNull Command serverCommand) {
        serverCommandList.add(serverCommand);
        return serverCommand;
    }

    private Command addClientCommand(@NotNull Command command) {
        clientCommandList.add(command);
        return command;
    }
}
