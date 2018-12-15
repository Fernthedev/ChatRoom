package com.github.fernthedev.server;

import com.github.fernthedev.packets.*;
import com.github.fernthedev.packets.latency.PongPacket;
import com.github.fernthedev.packets.message.MessagePacket;
import com.github.fernthedev.packets.message.RecieveMessagePacket;
import com.github.fernthedev.packets.message.SendMessagePacket;
import com.github.fernthedev.packets.player.*;
import com.github.fernthedev.universal.NetPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventListener {

    private Server server;

    private ClientPlayer clientPlayer;
    
    public EventListener(Server server, ClientPlayer clientPlayer) {
        this.server = server;
        this.clientPlayer = clientPlayer;
    }
    
    public void recieved(Object p) {

       // Server.getLogger().info(clientPlayer + " is the sender of packet");

        if(p instanceof SendMessagePacket) {
            //RecieveMessagePacket packet = (RecieveMessagePacket)p;
            SendMessagePacket sendMessagePacket = (SendMessagePacket)p;
            if(clientPlayer.getNetPlayer() == null) {
                clientPlayer.sendObject(new requestPlayerClassPacket("Null name. Resend"));
            }else {
                RecieveMessagePacket recieveMessagePacket = new RecieveMessagePacket(clientPlayer.getNetPlayer(),sendMessagePacket.message);

                Server.sendObjectToAllPlayers(recieveMessagePacket);
                Server.getLogger().info(clientPlayer.getNetPlayer().name + ":" + sendMessagePacket.message);
            }


        }
        else if(p instanceof TestConnectPacket) {
            TestConnectPacket packet = (TestConnectPacket) p;
            Server.getLogger().info("Connected packet: " + packet.getMessage());
        }

        else if(p instanceof ConnectedPacket) {
            ConnectedPacket packet = (ConnectedPacket)p;
            //Server.getLogger().info("Connected packet recieved from " + clientPlayer.getAdress());
            int id = 1;

            if(PlayerHandler.players.size() > 0) {
                while(id < PlayerHandler.players.size()) {
                    id++;
                }
            }

            if(!isAlpha(packet.name)) {
                disconnectIllegalName(packet,"Name requires alphabetical letters only");
            }

            for(NetPlayer netPlayer : PlayerHandler.players.values()) {
                if(netPlayer.name.equalsIgnoreCase(packet.name)) {
                    disconnectIllegalName(packet,"Name already in use");
                    return;
                }
            }

            if(Server.bannedNames.contains(packet.name)) {
                clientPlayer.sendObject(new MessagePacket("Your name is banned."));
                clientPlayer.close();
            }

            //Server.getLogger().info("Players: " + PlayerHandler.players.size());

            NetPlayer player = new NetPlayer(id,packet.name);

            clientPlayer.setNetPlayer(player);

            PlayerHandler.players.put(id, player);
            clientPlayer.sendObject(new sendNetPlayerPacket(player));

            clientPlayer.sendObject(new SendPlayerListPacket(PlayerHandler.players));
            Server.sendObjectToAllPlayers(new AddPlayerPacket(packet.name, player.id));

            clientPlayer.registered = true;

            Server.getLogger().info(player.name + " has joined the game");
            Server.getLogger().debug("NAME:ID " + player.name + ":" + player.id);
            Server.getLogger().debug(PlayerHandler.players.get(player.id).name + " the name." + PlayerHandler.players.get(player.id).id + " the id");
        } else if(p instanceof requestPlayerList) {
            clientPlayer.sendObject(new SendPlayerListPacket(PlayerHandler.players));
        } else if(p instanceof NullClass) {
            clientPlayer.sendObject(server.lastPacket);
        } else if(p instanceof PongPacket) {

            clientPlayer.endTime = System.nanoTime();

            clientPlayer.getNetPlayer().ping = TimeUnit.NANOSECONDS.toMillis(clientPlayer.endTime - clientPlayer.startTime);



        } else if (p instanceof CommandPacket) {

            CommandPacket packet = (CommandPacket) p;

            String command = packet.getMessage();

            String[] checkmessage = command.split(" ", 2);
            List<String> messageword = new ArrayList<>();

            String commandName = null;


            if (checkmessage.length > 1) {
                int index = 0;



                for(String message : checkmessage) {
                    index++;



                    if(index == 1 || message == null || message.equals("") || message.equals(" ")) {

                        if(!(message == null || message.equals("") || message.equals(" "))) {
                            commandName = message;
                        }

                        continue;
                    }


                    messageword.add(message);
                }
            }else
                commandName = checkmessage[0];


            for(Command commandCheck : CommandHandler.clientCommandList) {
                if(commandCheck.getCommandName().equals(commandName)) {
                    String[] args = new String[messageword.size()];
                    args = messageword.toArray(args);

                    Server.getLogger().info(clientPlayer + " /"+ commandCheck.getCommandName());

                    new Thread(new CommandHandler(clientPlayer,commandCheck,args)).start();
                    break;
                }
            }
        }
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private void disconnectIllegalName(ConnectedPacket packet,String message) {
        clientPlayer.sendObject(new IllegalNamePacket(packet.name,message));
        clientPlayer.close();
    }
}
