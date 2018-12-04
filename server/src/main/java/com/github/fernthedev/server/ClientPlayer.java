package com.github.fernthedev.server;

import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.SafeDisconnect;
import com.github.fernthedev.universal.NetPlayer;
import io.netty.channel.Channel;

import static com.github.fernthedev.server.Server.clientNetPlayerList;
import static com.github.fernthedev.server.Server.socketList;

public class ClientPlayer {
      //   Socket socket;
         String nickname;


    //private ObjectOutputStream out;
    //private ObjectInputStream in;

    private ServerThread thread;

    private boolean connected;

    private NetPlayer netPlayer;

    public Channel channel;

    public NetPlayer getNetPlayer() {
        return netPlayer;
    }

    public void setNetPlayer(NetPlayer netPlayer) {
        this.netPlayer = netPlayer;
    }

    public void setThread(ServerThread thread) {
        this.thread = thread;
    }

    public boolean isConnected() {
        return connected;
    }

    public ClientPlayer(Channel channel) {
            this.channel = channel;
    }


        String getNickname() {
            return nickname;
        }

        void setLastPacket(Object packet) {
            if(packet instanceof Packet) {
            }
        }

    public void sendObject(Object packet) {
        if (packet instanceof Packet) {

            channel.writeAndFlush(packet);
            // out.flush();
           /* if(!(packet instanceof PingPacket)) {
                Server.getLogger().info("Sent " + packet);
            }*/

        }else {
            Server.getLogger().info("not packet");
        }
    }

    public void close() {
        //DISCONNECT FROM SERVER
        if(channel != null) {
            Server.getLogger().info("Closing player " + this.toString());
            if(channel.isOpen()) {
                sendObject(new SafeDisconnect());
                channel.closeFuture();
            }

            socketList.remove(channel);
            PlayerHandler.players.remove(netPlayer.id);
            Server.channelServerHashMap.remove(channel);
            clientNetPlayerList.remove(this);
        }

        connected = false;
        try {
            Thread threadThing = thread.shutdown();

            if(threadThing != Thread.currentThread()) threadThing.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //serverSocket.close();
    }



    @Override
    public String toString() {

        if(netPlayer == null) {
            return "[ClientPlayer] IP: " + getAdress() + " but was not fully registered";
        }

        return "[ClientPlayer] IP: " + getAdress() + " name " + netPlayer.name + " id " + netPlayer.id;
    }

    public String getNameAddress() {
        return "[ClientPlayer] IP: " + getAdress();
    }

       String getAdress() {
        if(channel.remoteAddress() == null) {
            return "unknown";
        }

            return channel.remoteAddress().toString();
        }
}
