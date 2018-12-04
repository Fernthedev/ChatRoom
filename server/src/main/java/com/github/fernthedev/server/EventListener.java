package com.github.fernthedev.server;

import com.github.fernthedev.packets.*;
import com.github.fernthedev.universal.NetPlayer;

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
            if(sendMessagePacket.sender == null) {
                clientPlayer.sendObject(new requestPlayerClassPacket("Null name. Resend"));
            }else {
                RecieveMessagePacket recieveMessagePacket = new RecieveMessagePacket(sendMessagePacket.sender, sendMessagePacket.message);

                Server.sendObjectToAllPlayers(recieveMessagePacket);
                Server.getLogger().info(sendMessagePacket.sender.name + ":" + sendMessagePacket.message);
            }
        } else if(p instanceof PlayerLeave) {
            PlayerLeave packet = (PlayerLeave) p;
            Server.getLogger().info(clientPlayer.getNetPlayer().name + " has left the game");

            PlayerHandler.players.remove(clientPlayer.getNetPlayer().id);
            clientPlayer.close();
            Server.sendObjectToAllPlayers(new RemovePlayerPacket(clientPlayer.getNetPlayer()));

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

            Server.getLogger().info("Players: " + PlayerHandler.players.size());

            NetPlayer player = new NetPlayer(id,packet.name);

            clientPlayer.setNetPlayer(player);

            PlayerHandler.players.put(id, player);
            clientPlayer.sendObject(new sendNetPlayerPacket(player));

            clientPlayer.sendObject(new SendPlayerListPacket(PlayerHandler.players));
            Server.sendObjectToAllPlayers(new AddPlayerPacket(packet.name, player.id));


            Server.getLogger().info(player.name + " has joined the game");
            Server.getLogger().info("NAME:ID " + player.name + ":" + player.id);
            Server.getLogger().info(PlayerHandler.players.get(player.id).name + " the name." + PlayerHandler.players.get(player.id).id + " the id");
            
            /*for(int i = 0;i < 5;i++) {
                server.sendObject(new RecieveMessagePacket(new NetPlayer(0,"Fern"),"THIS IS A MESSAGE"));
            }*/
        } else if(p instanceof requestPlayerList) {
            clientPlayer.sendObject(new SendPlayerListPacket(PlayerHandler.players));
        } else if(p instanceof NullClass) {
            clientPlayer.sendObject(server.lastPacket);
        } else if(p instanceof PongPacket) {
            PongPacket packet = (PongPacket) p;
            long time = (System.nanoTime() - packet.getTime() );

            Server.getLogger().info("Ping: " + TimeUnit.NANOSECONDS.toMillis(time) + " ms");
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
