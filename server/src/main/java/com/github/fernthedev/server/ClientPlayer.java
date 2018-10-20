package com.github.fernthedev.server;

import com.github.fernthedev.universal.NetPlayer;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.SafeDisconnect;
import io.netty.channel.Channel;

import static com.github.fernthedev.server.Server.clientNetPlayerList;
import static com.github.fernthedev.server.Server.socketList;

public class ClientPlayer {
      //   Socket socket;
         String nickname;


    //private ObjectOutputStream out;
    //private ObjectInputStream in;

    private FernThread thread;

    private boolean connected;

    private NetPlayer netPlayer;

    public Channel channel;

    public NetPlayer getNetPlayer() {
        return netPlayer;
    }

    public void setNetPlayer(NetPlayer netPlayer) {
        this.netPlayer = netPlayer;
    }

    public void setThread(FernThread thread) {
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
                System.out.println("Sent " + packet);
            }*/

        }else {
            System.out.println("not packet");
        }
    }

    public void close(boolean isClosed,boolean sendObject) {
        try {
            thread.running = false;
            System.out.println("Closing player " + this.toString());
            //DISCONNECT FROM SERVER
            //RemovePlayerPacket packet = new RemovePlayerPacket();
            if(channel != null) {
                if(sendObject && channel.isOpen()) {
                    sendObject(new SafeDisconnect());
                }

                if (channel.isOpen()) {
                    channel.closeFuture();


                    socketList.remove(this);
                    clientNetPlayerList.remove(this);
                }
            }
            //if(!scanner.nextLine().equals(""))

            connected = false;
            thread.join();

            //serverSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String toString() {

        if(netPlayer == null) {
            return "[ClientPlayer] IP: " + getAdress() + " but was incorrectly registered";
        }

        return "[ClientPlayer] IP: " + getAdress() + " name " + netPlayer.name + " id " + netPlayer.id;
    }


        String getAdress() {
        if(channel.remoteAddress() == null) {
            return "unknown";
        }

            return channel.remoteAddress().toString();
        }
}
