package com.github.fernthedev.server;

import com.github.fernthedev.packets.LostServerConnectionPacket;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.PingPacket;
import com.github.fernthedev.packets.SafeDisconnect;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ServerThread implements Runnable {


    private boolean running = false;

    private boolean isConnected = false;

    static List<ClientPlayer> socketList = new ArrayList<ClientPlayer>();

    private Object lastPacket;

    public ClientPlayer clientPlayer;

    private EventListener listener;


    private Channel channel;

    private Thread thread;
    //private ReadListener readListener;

    private Server server;

    public ServerThread(Server server, Channel channel, ClientPlayer clientPlayer, EventListener listener) {
        this.server = server;
        this.clientPlayer = clientPlayer;
        this.listener = listener;
        thread = Thread.currentThread();
        running = true;
        isConnected = true;

        this.channel = channel;
        Server.serverInstanceThreads.add(thread);

    }

    void sendObject(Object packet) {
        if (packet instanceof Packet) {
            if (isConnected) {
                channel.writeAndFlush(packet);
                if(!(packet instanceof PingPacket)) {
                  //  Server.getLogger().info("Sent " + packet);

                    lastPacket = packet;
                }
            }
        }else {
            Server.getLogger().info("not packet");
        }
    }


    synchronized Thread shutdown() {
        try {
            running = false;
            //DISCONNECT FROM SERVER
            if (channel != null) {

                if ((!channel.isActive())) {
                    channel.closeFuture().sync();
                }


                socketList.remove(clientPlayer);
                Server.clientNetPlayerList.remove(clientPlayer);


            }

            isConnected = false;


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return thread;
    }

    synchronized Thread close(boolean sendObject) {
        try {
            Server.getLogger().info("Closing connection at for player " + clientPlayer);
            running = false;
            //DISCONNECT FROM SERVER
            //RemovePlayerPacket packet = new RemovePlayerPacket();
            if(channel != null) {

                if(sendObject && channel.isActive()) {
                    Server.getLogger().info("Sent disconnect");
                    sendObject(new SafeDisconnect());
                }

                if ((!channel.isActive())) {


                    Server.getLogger().info("Closing sockets.");

                    channel.closeFuture().sync();

                    socketList.remove(clientPlayer);
                    Server.clientNetPlayerList.remove(clientPlayer);

                    Server.getLogger().info("Closed sockets ");
                }
            }

            isConnected = false;



        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return thread;
    }

    int secondsPassed;

    public void run() {
        if(!running) {
            Server.sendObjectToAllPlayers(new LostServerConnectionPacket());
            close(false);

        }
        //Server.getLogger().info("Checking for " + clientPlayer + " socket " + channel);


        long time = System.nanoTime();

        // And From your main() method or any other method
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                secondsPassed++;
            }
        }, 20, 1000);


        while (running) {

            if(secondsPassed >= 5) {
                sendObject(new PingPacket());
                secondsPassed = 0;
                long nowtime = (System.nanoTime() - time);
                time = System.nanoTime();
                Server.getLogger().info("Took " + TimeUnit.NANOSECONDS.toMillis(nowtime) + " ms");
            }
        }
    }

    ClientPlayer getPlayer(String adress) {
        for(ClientPlayer clientPlayerThing : socketList) {
            if(clientPlayerThing.getAdress().equals(adress)) return clientPlayerThing;
        }

        return null;
    }

    boolean isRunning() {
        return running;
    }

}
