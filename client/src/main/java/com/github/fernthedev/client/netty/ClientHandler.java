package com.github.fernthedev.client.netty;

import com.github.fernthedev.client.Client;
import com.github.fernthedev.client.EventListener;
import com.github.fernthedev.packets.ConnectedPacket;
import com.github.fernthedev.packets.Packet;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private EventListener listener;

    public ClientHandler(Client client,EventListener listener) {
        this.listener = listener;
        this.client = client;
    }

    private Client client;

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {


        client.registered = true;



        ChannelFuture future = ctx.writeAndFlush(new ConnectedPacket(client.name));
        Client.getLogger().info("Sent connect packet for request");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        if(msg instanceof Packet) {
            Packet packet = (Packet) msg;
            listener.recieved(packet);

            /*if (!(msg instanceof PingPacket))
                client.getLogger().info("Packet received which is " + msg);*/
        }

        //ctx.close();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Lost connection to server.");
        client.getClientThread().close();
    }
}
