package com.github.fernthedev.client;

import com.github.fernthedev.universal.NetPlayer;
import com.github.fernthedev.packets.*;

import java.util.concurrent.TimeUnit;

public class EventListener {

    private Client client;

    EventListener(Client client) {
        this.client = client;
    }

    public void recieved(Object p) {
        if(p instanceof AddPlayerPacket) {

            AddPlayerPacket packet = (AddPlayerPacket) p;
            NetPlayer newPlayer = new NetPlayer(packet.id,packet.name);

            if((newPlayer.id != client.player.id)) {

                if (!PlayerHandler.players.containsValue(newPlayer))
                    PlayerHandler.players.put(packet.id, new NetPlayer(packet.id, packet.name));
                System.out.println(packet.name + " has joined the game");
            }else{
                System.out.println("Server has registered client in list");
            }
        }


        else if(p instanceof TestConnectPacket) {
            TestConnectPacket packet = (TestConnectPacket) p;
            System.out.println("Connected packet: " + packet.getMessage());
        }

        else if(p instanceof RemovePlayerPacket) {
            RemovePlayerPacket packet = (RemovePlayerPacket)p;
            if(PlayerHandler.players.containsValue(new NetPlayer(packet.id,PlayerHandler.players.get(packet.id).name))) {
                System.out.println(PlayerHandler.players.get(packet.id).name + " has left the game");
                PlayerHandler.players.remove(packet.id);
            }
        }


        else if(p instanceof RecieveMessagePacket) {
            RecieveMessagePacket packet = (RecieveMessagePacket)p;
            System.out.println(packet.sender.name + ":" + packet.message);
            /*if((PlayerHandler.players.containsKey(packet.sender.id)) && (PlayerHandler.players.get(packet.sender.id).name.equals(packet.sender.name))) {

            }else {
                System.out.println("An unknown player sent packet. Player is not in playlist, discarding packet.");
            }*/

        }

        else if(p instanceof LostServerConnectionPacket) {
            LostServerConnectionPacket packet = (LostServerConnectionPacket)p;
            System.out.println("Lost connection to server! Must have shutdown!");
            PlayerHandler.players.clear();
            client.getClientThread().disconnect();
        }

        else if (p instanceof sendNetPlayerPacket) {
            sendNetPlayerPacket packet = (sendNetPlayerPacket) p;
            System.out.println("SERVER SENT US THE PLAYER NAME AND ID!");
            System.out.println("ID:" + packet.player.id);
            System.out.println("NAME:" + packet.player.name);
            client.player = packet.player;
        }

        else if(p instanceof SendPlayerListPacket) {
            SendPlayerListPacket packet = (SendPlayerListPacket)p;
            PlayerHandler.players = packet.players;
        }else if(p instanceof requestPlayerClassPacket) {
            requestPlayerClassPacket packet = (requestPlayerClassPacket)p;
            client.getClientThread().sendObject(new ConnectedPacket(client.player.name));
        } else if(p instanceof PingPacket) {
            //System.out.println("Ponged!");
            PingPacket packet = (PingPacket) p;

            long time = (System.nanoTime() - packet.getTime() ) / 1000000;

            System.out.println("Ping: " + TimeUnit.MILLISECONDS.convert(time,TimeUnit.NANOSECONDS) + " ms");
            client.getClientThread().sendObject(new PongPacket());
        } else if(p instanceof RequestNamePacket) {

                client.getClientThread().sendObject(new ConnectedPacket(client.name));
                client.registered = true;

        } else if(p instanceof SafeDisconnect) {
            System.out.println("Shutting down due to server disconnect");
            client.getClientThread().disconnect();
        }
    }

}
