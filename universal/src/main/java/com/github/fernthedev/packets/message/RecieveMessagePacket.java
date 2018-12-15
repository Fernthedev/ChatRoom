package com.github.fernthedev.packets.message;

import com.github.fernthedev.packets.Packet;
import com.github.fernthedev.universal.NetPlayer;

import java.io.Serializable;

public class RecieveMessagePacket extends Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    public String message;
    public NetPlayer sender;

    public RecieveMessagePacket(NetPlayer netPlayer, String message) {
        this.sender = netPlayer;
        this.message = message;
    }
}
