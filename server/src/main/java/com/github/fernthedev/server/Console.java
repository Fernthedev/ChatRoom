package com.github.fernthedev.server;

import com.github.fernthedev.packets.Packet;

public class Console implements CommandSender {

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void sendMessage(String message) {
        Server.getLogger().info(message);
    }
}
