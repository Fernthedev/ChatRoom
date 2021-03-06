package com.github.fernthedev.client;

import com.github.fernthedev.packets.*;
import com.github.fernthedev.packets.latency.PingPacket;
import com.github.fernthedev.packets.latency.PingReceive;
import com.github.fernthedev.packets.latency.PongPacket;
import com.github.fernthedev.packets.message.MessagePacket;
import com.github.fernthedev.packets.message.RecieveMessagePacket;
import com.github.fernthedev.packets.player.*;
import com.github.fernthedev.universal.NetPlayer;

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
                Client.getLogger().info(packet.name + " has joined the game");
            }else{
                Client.getLogger().debug("Server has registered client in list");
            }
        }


        else if(p instanceof TestConnectPacket) {
            TestConnectPacket packet = (TestConnectPacket) p;
            Client.getLogger().info("Connected packet: " + packet.getMessage());
        }

        else if(p instanceof RemovePlayerPacket) {
            RemovePlayerPacket packet = (RemovePlayerPacket)p;
            if(PlayerHandler.players.containsValue(new NetPlayer(packet.id,PlayerHandler.players.get(packet.id).name))) {
                Client.getLogger().info(PlayerHandler.players.get(packet.id).name + " has left the game");
                PlayerHandler.players.remove(packet.id);
            }
        }


        else if(p instanceof RecieveMessagePacket) {
            RecieveMessagePacket packet = (RecieveMessagePacket)p;
            Client.getLogger().info(packet.sender.name + ":" + packet.message);
            /*if((PlayerHandler.players.containsKey(packet.sender.id)) && (PlayerHandler.players.get(packet.sender.id).name.equals(packet.sender.name))) {

            }else {
                Client.getLogger().info("An unknown player sent packet. Player is not in playlist, discarding packet.");
            }*/

        }

        else if(p instanceof LostServerConnectionPacket) {
            LostServerConnectionPacket packet = (LostServerConnectionPacket)p;
            Client.getLogger().info("Lost connection to server! Must have shutdown!");
            PlayerHandler.players.clear();
            client.getClientThread().disconnect();
        }

        else if (p instanceof sendNetPlayerPacket) {
            sendNetPlayerPacket packet = (sendNetPlayerPacket) p;
            Client.getLogger().debug("SERVER SENT US THE PLAYER NAME AND ID!");
            Client.getLogger().debug("ID:" + packet.player.id);
            Client.getLogger().debug("NAME:" + packet.player.name);
            client.player = packet.player;
        }

        else if(p instanceof SendPlayerListPacket) {
            SendPlayerListPacket packet = (SendPlayerListPacket)p;
            PlayerHandler.players = packet.players;
        }else if(p instanceof requestPlayerClassPacket) {
            requestPlayerClassPacket packet = (requestPlayerClassPacket)p;
            client.getClientThread().sendObject(new ConnectedPacket(client.player.name));
        } else if(p instanceof PingPacket) {
            ClientThread.startTime = System.nanoTime();

            client.getClientThread().sendObject(new PongPacket());
        } else if(p instanceof PingReceive) {

            ClientThread.endTime = System.nanoTime();

            ClientThread.miliPingDelay = ClientThread.endTime - ClientThread.startTime;

            Client.getLogger().debug("Ping: " + TimeUnit.NANOSECONDS.toMillis(ClientThread.miliPingDelay) + "ms");

        } else if(p instanceof RequestNamePacket) {

                client.getClientThread().sendObject(new ConnectedPacket(client.name));
                client.registered = true;

        } else if(p instanceof SafeDisconnect) {
            Client.getLogger().info("Shutting down due to server disconnect");
            client.getClientThread().disconnect();
        } else if(p instanceof IllegalNamePacket) {
            IllegalNamePacket illegalNamePacket = (IllegalNamePacket) p;
            Client.getLogger().info("Illegal name. ("+illegalNamePacket.getName()+") Reason:" + illegalNamePacket.getMessage());


        } else if (p instanceof MessagePacket) {
            MessagePacket messagePacket = (MessagePacket) p;
            Client.getLogger().info(messagePacket.getMessage());
        }
    }

}
