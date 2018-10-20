package com.github.fernthedev.packets;


import com.github.fernthedev.universal.NetPlayer;

import java.io.Serializable;

public class SendMessagePacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    //public CNetPlayer sender;

    public String message;
    public NetPlayer sender;

    public SendMessagePacket(NetPlayer sender,String message) {
        this.message = message;
        this.sender = sender;
    }
}
