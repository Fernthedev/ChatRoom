package com.github.fernthedev.server.netty;

import com.github.fernthedev.packets.LostServerConnectionPacket;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.server.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class ProcessingHandler extends ChannelInboundHandlerAdapter {



    private List<Object> packetsLost = new ArrayList<>();

    private Server server;

    public ProcessingHandler(Server server) {this.server = server;}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        Packet requestData = (Packet) msg;
        /*if(eventListener == null) {
            packetsLost.add(msg);
        }*/
        boolean found = false;
        for(Channel channel : Server.socketList.keySet()) {
            if(channel == ctx.channel()) {
                System.out.println("Found the current channel");
                found = true;
            }
        }

        if(!found) {
            System.out.println("No channel associated with me?");
        }

        EventListener eventListener = new EventListener(server,Server.socketList.get(ctx.channel()));

        for(Object packetLos : packetsLost){
            eventListener.recieved(packetLos);
        }

        System.out.println(ctx.channel());

        eventListener.recieved(requestData);

        //ChannelFuture future = ctx.writeAndFlush(responseData);
        //future.addListener(ChannelFutureListener.CLOSE);
        /*if(!(requestData instanceof PongPacket))
        System.out.println("Received this packet " + msg);*/

        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
        ctx.fireChannelReadComplete();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if(!ctx.channel().isActive()) {
            try {
                ctx.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Channel Registering");
        Channel channel = ctx.channel();

        if (channel != null) {
            Server server = Server.channelServerHashMap.get(ctx.channel());




            ClientPlayer clientPlayer = new ClientPlayer(channel);

            System.out.println("Created clientPlayer variable " + clientPlayer + " channel " + channel);

            Server.socketList.put(channel,clientPlayer);


            EventListener listener = new EventListener(server, clientPlayer);
            //processingHandler.setListener(listener);
            System.out.println("Events registered");


// And From your main() method or any other method
            FernThread runningFernThread;


            ServerThread serverThread = new ServerThread(server, channel, clientPlayer, listener);

            runningFernThread = new FernThread(serverThread);
            clientPlayer.setThread(runningFernThread);

            runningFernThread.startThread();
            //serverThread.startListener();

            System.out.println("Thread started for player " + clientPlayer);


            //clientPlayer.sendObject(new RequestNamePacket());

            Runtime.getRuntime().addShutdownHook(new FernThread() {
                @Override
                public void run() {
                    for (ServerThread serverThread : Server.serverThreads) {
                        if (serverThread.clientPlayer.channel.isOpen()) {
                            System.out.println("Gracefully shutting down/");
                            Server.sendObjectToAllPlayers(new LostServerConnectionPacket());
                            serverThread.clientPlayer.close(false, false);
                        }
                    }
                }
            });
        }else{
            System.out.println("Channel is null");
            throw new NullPointerException();
        }
    }

}
