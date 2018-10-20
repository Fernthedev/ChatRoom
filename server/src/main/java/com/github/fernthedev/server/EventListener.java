package com.github.fernthedev.server;

import com.github.fernthedev.universal.NetPlayer;

import java.util.concurrent.TimeUnit;
import com.github.fernthedev.packets.*;
public class EventListener {

    private Server server;

    private ClientPlayer clientPlayer;
    
    public EventListener(Server server, ClientPlayer clientPlayer) {
        this.server = server;
        this.clientPlayer = clientPlayer;
    }
    
    public void recieved(Object p) {

        System.out.println(clientPlayer + " is the sender of packet");

        if(p instanceof SendMessagePacket) {
            //RecieveMessagePacket packet = (RecieveMessagePacket)p;
            SendMessagePacket sendMessagePacket = (SendMessagePacket)p;
            if(sendMessagePacket.sender == null) {
                clientPlayer.sendObject(new requestPlayerClassPacket("Null name. Resend"));
            }else {
                RecieveMessagePacket recieveMessagePacket = new RecieveMessagePacket(sendMessagePacket.sender, sendMessagePacket.message);

                Server.sendObjectToAllPlayers(recieveMessagePacket);
                System.out.println(sendMessagePacket.sender.name + ":" + sendMessagePacket.message);
            }
        } else if(p instanceof PlayerLeave) {
            PlayerLeave packet = (PlayerLeave) p;
            System.out.println(clientPlayer.getNetPlayer().name + " has left the game");

            PlayerHandler.players.remove(clientPlayer.getNetPlayer().id);
            clientPlayer.close(false,true);
            Server.sendObjectToAllPlayers(new RemovePlayerPacket(clientPlayer.getNetPlayer()));

        }/*else if(p instanceof RemovePlayerPacket) {
            RemovePlayerPacket packet = (RemovePlayerPacket) p;
            System.out.println(com.github.fernthedev.client.PlayerHandler.players.get(packet.id).name + " has left the game");
            PlayerHandler.players.remove(packet.id);

            Server.sendObjectToAllPlayers(packet);
        }*/
        else if(p instanceof TestConnectPacket) {
            TestConnectPacket packet = (TestConnectPacket) p;
            System.out.println("Connected packet: " + packet.getMessage());
        }

        else if(p instanceof ConnectedPacket) {
            ConnectedPacket packet = (ConnectedPacket)p;
            System.out.println("Connected packet recieved from " + clientPlayer.getAdress());
            int id = 1;
            System.out.println("Players: " + PlayerHandler.players.size());
            if(PlayerHandler.players.size() > 0) {
                while(id < PlayerHandler.players.size()) {
                    id++;
                }
            }

            NetPlayer player = new NetPlayer(id,packet.name);

            clientPlayer.setNetPlayer(player);

            PlayerHandler.players.put(id, player);
            clientPlayer.sendObject(new sendNetPlayerPacket(player));

            clientPlayer.sendObject(new SendPlayerListPacket(PlayerHandler.players));
            Server.sendObjectToAllPlayers(new AddPlayerPacket(packet.name, player.id));


            System.out.println(player.name + " has joined the game");
            System.out.println("NAME:ID " + player.name + ":" + player.id);
            System.out.println(PlayerHandler.players.get(player.id).name + " the name." + PlayerHandler.players.get(player.id).id + " the id");
            
            /*for(int i = 0;i < 5;i++) {
                server.sendObject(new RecieveMessagePacket(new NetPlayer(0,"Fern"),"THIS IS A MESSAGE"));
            }*/
        } else if(p instanceof requestPlayerList) {
            clientPlayer.sendObject(new SendPlayerListPacket(PlayerHandler.players));
        } else if(p instanceof NullClass) {
            clientPlayer.sendObject(server.lastPacket);
        } else if(p instanceof PongPacket) {
            PongPacket packet = (PongPacket) p;
            long time = (System.nanoTime() - packet.getTime() ) / 1000000;

            System.out.println("Ping: " + TimeUnit.NANOSECONDS.toMillis(time) + " ms");
        }
    }

}
