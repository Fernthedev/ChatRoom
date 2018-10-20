package com.github.fernthedev.packets;

import com.github.fernthedev.universal.NetPlayer;

import java.io.Serializable;

public class RecieveMessagePacket extends Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    public NetPlayer sender;

    public String message;

    public RecieveMessagePacket(NetPlayer sender, String message) {
     this.sender = sender;
     this.message = message;
    }
}
