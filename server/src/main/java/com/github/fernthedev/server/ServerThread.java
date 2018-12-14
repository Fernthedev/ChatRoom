package com.github.fernthedev.server;

import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.latency.PingPacket;
import com.github.fernthedev.packets.SafeDisconnect;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable {


    private boolean running;

    private boolean isConnected;

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
            }
            socketList.remove(clientPlayer);
            Server.clientNetPlayerList.remove(clientPlayer);
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

    private int secondsPassed;

    public void run() {


       // long time = System.nanoTime();

        // And From your main() method or any other method
        /*Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!running) {
                    timer.purge();
                    timer.cancel();
                }
                secondsPassed++;
                System.out.println("Second passed " + running);
            }
        }, (long) 2*1000, (long) 2*1000);*/


        while (running) {
            secondsPassed++;
            //System.out.println("Checking " + secondsPassed);
            if(secondsPassed >= 5) {
                //Server.getLogger().info("Sending packet");
                clientPlayer.ping();
                secondsPassed = 0;
                //long nowtime = (System.nanoTime() - time);
                //time = System.nanoTime();
                //Server.getLogger().info("Took " + TimeUnit.NANOSECONDS.toMillis(nowtime) + " ms");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
