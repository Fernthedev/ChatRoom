package com.github.fernthedev.server;

import com.github.fernthedev.universal.NetPlayer;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.server.netty.ProcessingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.*;

public class Server {

    private int port;
    //private Socket clientSocket;
    //private ServerSocket serverSocket;

    private boolean running = false;

    // private ObjectOutputStream out;
   // private ObjectInputStream in;

    public static Map<Channel,ClientPlayer> socketList = new HashMap<>();

    static Map<ClientPlayer,NetPlayer> clientNetPlayerList = new HashMap<>();

    public static Map<Channel,Server> channelServerHashMap = new HashMap<>();

    public static List<ServerThread> serverThreads = new ArrayList<>();

    static List<Thread> serverInstanceThreads = new ArrayList<>();


    Object lastPacket;

    private ChannelFuture future;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup,workerGroup;

    static NetPlayer serverPlayer;

    private ProcessingHandler processingHandler;

    public Server(int port) {
        this.port = port;

    }

    public void startServer() {
        //serverSocket = new ServerSocket(port);
        bossGroup = new NioEventLoopGroup();
       processingHandler = new ProcessingHandler(this);
         workerGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {

                        ch.pipeline().addLast(new ObjectEncoder(),
                                new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                processingHandler);
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY,true);


                 /*finally {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                }*/


        running = true;
        System.out.println("Server socket registered");
        new Thread(new ServerBackground(this)).start();
        //Timer pingPongTimer = new Timer("pingpong");
        System.out.println("Server started successfully at localhost");

        serverPlayer = new NetPlayer(0,"Server");
        PlayerHandler.players.put(serverPlayer.id,serverPlayer);

        //connect();
        try {
            future = bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (!future.isSuccess()) {
            System.out.println("Failed to bind port");
        }else{
            System.out.println("Binded port on " + future.channel().localAddress());
        }

        connect();
    }


    private void connect() {

        while (running) {
            try {


                future = future.await().sync();

                future.channel().closeFuture().sync();


                if(future.channel().isActive() && future.channel().isRegistered()) {
                    channelServerHashMap.put(future.channel(),this);
                }

                /*

                future.addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        channel = channelFuture.channel();


                        System.out.println("Connected to channel " + future.channel());
                        establishClient(future);

                        channelFuture.channel().closeFuture().addListener((ChannelFutureListener) channelFuture1 -> {
                            future.channel().closeFuture().sync();
                            channel.closeFuture().sync();
                        });

                    }
                });*/

                //future.channel().closeFuture().sync();
                //future.channel().closeFuture().sync();


            /*if (!future.isSuccess()) {
                System.out.println("Failed to bind port");
            }*/

                // if (future.channel().isRegistered()) {
                //         System.out.println("Connected to channel " + future.channel().remoteAddress());
                //    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    public static void sendObjectToAllPlayers(Object packet) {
        for(Channel channel : socketList.keySet()) {

            ClientPlayer clientPlayer = socketList.get(channel);

            if (packet instanceof Packet) {
                if (clientPlayer.channel.isActive()) {
                    clientPlayer.sendObject(packet);
                }
                clientPlayer.setLastPacket(packet);
            } else {
                System.out.println("not packet");
            }
        }
    }


    /*
    @Deprecated
    private void establishClient(ChannelFuture channelFuture) {

        if (channel != null) {
            connected = true;

            ClientPlayer clientPlayer = new ClientPlayer(channelFuture);

            System.out.println("Created clientPlayer variable " + clientPlayer + " channel " + future.channel());

            socketList.put(channelFuture.channel(),clientPlayer);


            listener = new EventListener(this, clientPlayer);
            //processingHandler.setListener(listener);
            System.out.println("Events registered");


// And From your main() method or any other method
            FernThread runningFernThread;


            ServerThread serverThread = new ServerThread(this, channelFuture, clientPlayer, listener);

            runningFernThread = new FernThread(serverThread);
            clientPlayer.setThread(runningFernThread);

            runningFernThread.startThread();
            serverThread.startListener();

            System.out.println("Thread started for player " + clientPlayer);


            clientPlayer.sendObject(new RequestNamePacket());

            Runtime.getRuntime().addShutdownHook(new FernThread() {
                @Override
                public void run() {
                    for (ServerThread serverThread : serverThreads) {
                        if (serverThread.clientPlayer.channel.isOpen()) {
                            System.out.println("Gracefully shutting down/");
                            sendObjectToAllPlayers(new LostServerConnectionPacket());
                            serverThread.clientPlayer.close(false, false);
                        }
                    }
                }
            });
        }else{
            System.out.println("Channel is null");
            throw new NullPointerException();
        }
    }*/


    void shutdownServer() {
        for (ServerThread thread : serverThreads) {
            thread.close(true);
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    boolean isRunning() {
        return running;
    }
}
