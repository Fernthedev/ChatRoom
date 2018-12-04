package com.github.fernthedev.server;

import com.github.fernthedev.packets.MessagePacket;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.SafeDisconnect;
import com.github.fernthedev.universal.NetPlayer;
import io.netty.channel.Channel;

import static com.github.fernthedev.server.Server.clientNetPlayerList;
import static com.github.fernthedev.server.Server.socketList;

public class ClientPlayer implements CommandSender{
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
        Server.getLogger().info("Closing player " + this.toString());

        if(channel != null) {
            channel.close();

            if(channel.isOpen()) {
                sendObject(new SafeDisconnect());
                channel.closeFuture();
            }
            socketList.remove(channel);
            Server.channelServerHashMap.remove(channel);
        }

        PlayerHandler.players.remove(netPlayer.id);
        clientNetPlayerList.remove(this);

        connected = false;
        Thread threadThing = thread.shutdown();

        Server.closeThread(threadThing);

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

    @Override
    public void sendPacket(Packet packet) {
        sendObject(packet);
    }

    @Override
    public void sendMessage(String message) {
        sendPacket(new MessagePacket(message));
    }
}
