package com.github.fernthedev.packets.message;


import com.github.fernthedev.packets.Packet;

import java.io.Serializable;

public class SendMessagePacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    //public CNetPlayer sender;

    public String message;

    public SendMessagePacket(String message) {
        this.message = message;
    }
}
