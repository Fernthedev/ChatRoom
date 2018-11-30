package com.github.fernthedev.client;

import com.github.fernthedev.client.netty.ClientHandler;
import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.packets.PlayerLeave;
import com.github.fernthedev.packets.RemovePlayerPacket;
import com.github.fernthedev.universal.NetPlayer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ClientThread implements Runnable {
    NetPlayer player;

    private boolean isRegistered() {
        return client.registered;
    }


    boolean running;

     boolean connected;


    private EventListener listener;
    private Client client;

    boolean connectToServer;

    private Thread readingThread;

    private ChannelFuture future;
    private Channel channel;

    private EventLoopGroup workerGroup;

    //private ReadListener readListener;


    public ClientThread(Client client) {
        this.client = client;
        listener = new EventListener(client);
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    void connect() {
        System.out.println("Connecting to server.");

        workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            new ClientHandler(client, listener));
                }
            });

            future = b.connect(client.host, client.port).sync();
            channel = future.channel();




            //future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        if (future.isSuccess() && future.channel().isActive()) {
            System.out.println("SOCKET CONNECTED!");
            connected = true;


            if (!Client.currentThread.isAlive()) {
                running = true;
                Client.currentThread.start();
                System.out.println("This thread started");
            }

            if(!waitForCommand.running) {
                client.running = true;
                System.out.println("NEW WAIT FOR COMMAND THREAD");
                Client.waitThread = new Thread(Client.WaitForCommand);
                Client.waitThread.start();
                client.print("Command thread started");
            }

            //setReadListener();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (channel.isActive() && player != null) {
                    sendObject(new RemovePlayerPacket(player));
                    disconnect();
                }
                for (Thread thread : Thread.getAllStackTraces().keySet()) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));

        }
    }



    void sendObject(Object packet) {
        if (packet instanceof Packet) {
            if (channel.isActive()) {
                channel.writeAndFlush(packet);

              /*  if(!(packet instanceof PongPacket)) {
                    System.out.println("Sent an object which is " + packet);
                }*/
            }
        }else {
            System.out.println("not packet");
        }
    }

    void disconnect() {
        System.out.println("Disconnecting from server");
        running = false;
        try {

            future.channel().closeFuture().sync();
            workerGroup.shutdownGracefully();

            System.out.println("Disconnected");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            client.print("Closing connection.");
            running = false;

            //DISCONNECT FROM SERVER
            if(channel.isActive()) {
                PlayerLeave packet = new PlayerLeave();

                if(connected) {
                    client.print("Sent disconnect packet.");
                    sendObject(packet);
                }

                if(channel.isActive()) {
                    //in.close();
                    // out.close();
                    channel.closeFuture().sync();
                    client.print("Closed connection.");
                }
            }

            for(Thread thread : Thread.getAllStackTraces().keySet()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Closing client!");
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        //client.print(running);
        client.print("Checking for " + client.host + ":" + client.port + " socket " + channel);
        while (running) {
            if (System.console() == null) close();
            /*
            try {
                //client.print("checking");
                    if (!isRegistered() && socket.isConnected()) {
                        sendObject(new ConnectedPacket(client.name));
                        client.registered = true;
                    }
                    if(in.available() != 0) {
                        client.print(in.available());
                    }


                   // boolean keepCheck = true;

                while (in.available() > 0) {

                   // if(in.read() == -1) {keepCheck = false;}

                    System.out.println("Something in the mailbox");
                            Object data = in.readObject();
                            if (data == null) {
                                sendObject(new NullClass());
                            } else {
                                client.print("Recieved");
                                listener.recieved(data);
                            }
                        }



                //  }
            } catch (UnknownHostException e) {
                disconnect();
            } catch (SocketException e) {
                e.printStackTrace();
                close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                client.print("LOST CONNECTION TO SERVER! RETRYING!");
                client.initialize();
                connected = false;
                //throwException();

            } catch (ClassCastException e) {
                e.printStackTrace();
                sendObject(new NullClass());
            } catch (EOFException e) {
                sendObject(new PlayerLeave());
                e.printStackTrace();
            } finally {
                client.print(running);
                client.print(Thread.getAllStackTraces());
                client.print(Thread.currentThread());
                client.print(Client.currentThread);
                client.print(Thread.currentThread().equals(Client.currentThread));
            }*/
        }

    }

    void setReadListener() {
        //readListener = new ReadListener(socket,in,out);

        //readingThread = new Thread(readListener);
        //readingThread.start();
    }


   /* private class ReadListener implements Runnable {

        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        private boolean running = false;

        public ReadListener(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
            this.socket = socket;
            this.in = in;
            this.out = out;
            running = true;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    try {
                        //client.print("checking");
                        if (!isRegistered() && socket.isConnected()) {
                            sendObject(new ConnectedPacket(client.name));
                            client.registered = true;
                        }

                        // boolean keepCheck = true;


                            // if(in.read() == -1) {keepCheck = false;}

                            //System.out.println("Something in the mailbox");
                        synchronized (in) {
                                Object data = in.readObject();

                                if (data == null) {
                                    synchronized (out) {
                                        sendObject(new NullClass());
                                    }
                                } else {
                                    if (!(data instanceof PingPacket)) {
                                        client.print("Recieved");
                                    }
                                    listener.recieved(data);
                                }

                        }

                        //  }
                    } catch (StreamCorruptedException e) {

                        e.printStackTrace();
                        //sendObject(new NullClass());

                    }catch (UnknownHostException e) {
                        disconnect();
                    } catch (SocketException e) {
                        e.printStackTrace();
                        close();
                    } catch (ClassNotFoundException | EOFException e) {
                        e.printStackTrace();
                    } catch (SocketTimeoutException e) {
                        client.print("LOST CONNECTION TO SERVER! RETRYING!");
                        client.initialize();
                        connected = false;
                        //throwException();

                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        sendObject(new NullClass());
                    } /* finally {
                    client.print(running);
                    client.print(Thread.getAllStackTraces());
                    client.print(Thread.currentThread());
                    client.print(Client.currentThread);
                    client.print(Thread.currentThread().equals(Client.currentThread));
                }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}
