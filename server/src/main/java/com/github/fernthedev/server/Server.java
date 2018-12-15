package com.github.fernthedev.server;

import com.github.fernthedev.packets.LostServerConnectionPacket;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.message.MessagePacket;
import com.github.fernthedev.server.netty.MulticastServer;
import com.github.fernthedev.server.netty.ProcessingHandler;
import com.github.fernthedev.universal.NetPlayer;
import com.github.fernthedev.universal.StaticHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server extends Canvas implements Runnable {

    private int port;

    public static final int WIDTH = 640,HEIGHT =  WIDTH / 12*9;

    private boolean running = false;

    private static Thread thread;

    private ServerBackground serverBackground;

    private Console console;

    public Console getConsole() {
        return console;
    }

    public static Map<Channel,ClientPlayer> socketList = new HashMap<>();

    public static Map<ClientPlayer,NetPlayer> clientNetPlayerList = new HashMap<>();

    public static Map<Channel,Server> channelServerHashMap = new HashMap<>();

    public static List<ServerThread> serverThreads = new ArrayList<>();

    static List<Thread> serverInstanceThreads = new ArrayList<>();

    public static List<String> bannedIps = new ArrayList<>();
    public static List<String> bannedNames = new ArrayList<>();


    Object lastPacket;

    private static final Logger logger = Logger.getLogger(Server.class);

    private ChannelFuture future;
    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup,workerGroup;

    static NetPlayer serverPlayer;

    private ProcessingHandler processingHandler;


    Server(int port) {
        this.port = port;
        console = new Console();
    }



    private void await() {
        Server server = this;
        new Thread(() -> {
            while (running) {
                try {
                    future = future.await().sync();

                    future.channel().closeFuture().sync();


                    if (future.channel().isActive() && future.channel().isRegistered()) {
                        channelServerHashMap.put(future.channel(), server);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static synchronized void sendObjectToAllPlayers(Object packet) {
        for(Channel channel : socketList.keySet()) {

            ClientPlayer clientPlayer = socketList.get(channel);

            if (packet instanceof Packet) {
                if (clientPlayer.channel.isActive()) {
                    clientPlayer.sendObject(packet);
                }
                clientPlayer.setLastPacket(packet);
            } else {
                logger.info("not packet");
            }
        }
    }


    synchronized void shutdownServer() {
        running = false;
        for (ServerThread thread : serverThreads) {
            try {
                Thread threadThing = thread.close(true);

                if(threadThing != Thread.currentThread()) threadThing.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        System.exit(0);
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        thread = Thread.currentThread();
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
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY,true)
        .childOption(ChannelOption.SO_TIMEOUT,5000);


        running = true;
        logger.info("Server socket registered");
        serverBackground = new ServerBackground(this);
        new Thread(serverBackground).start();
        //Timer pingPongTimer = new Timer("pingpong");



        serverPlayer = new NetPlayer(0,"Server");
        PlayerHandler.players.put(serverPlayer.id,serverPlayer);


        //await();
        try {
            future = bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Runtime.getRuntime().addShutdownHook(new FernThread() {
            @Override
            public void run() {
                for (ServerThread serverThread : Server.serverThreads) {
                    if (serverThread.clientPlayer.channel.isOpen()) {
                        Server.getLogger().info("Gracefully shutting down/");
                        Server.sendObjectToAllPlayers(new LostServerConnectionPacket());
                        serverThread.clientPlayer.close();
                    }
                }
            }
        });

        try {
            logger.info("Server started successfully at localhost (Connect with " + InetAddress.getLocalHost().getHostAddress() + ") using port " + port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (!future.isSuccess()) {
            logger.info("Failed to bind port");
        }else{
            logger.info("Binded port on " + future.channel().localAddress());
        }

        try {
            new MulticastServer("Multicast",this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        await();
        while(running) {
            tick();
        }
    }


    private void tick() {
        if (System.console() == null && !StaticHandler.isDebug) shutdownServer();
    }

    public static synchronized void closeThread(Thread thread) {
        if(thread == Server.thread) throw new IllegalArgumentException("Cannot be same thread it's joining on");

        Thread thread1 = new Thread(() -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e.getCause());
            }
        });

        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        Server.getLogger().info(message);
        Server.sendObjectToAllPlayers(new MessagePacket(message));
    }

    public int getPort() {
        return port;
    }

    public static Logger getLogger() {
        return logger;
    }
}
