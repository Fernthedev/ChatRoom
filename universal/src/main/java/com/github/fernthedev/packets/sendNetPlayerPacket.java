package com.github.fernthedev.packets;



import com.github.fernthedev.universal.NetPlayer;

import java.io.Serializable;

public class sendNetPlayerPacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    public NetPlayer player;

    public sendNetPlayerPacket(NetPlayer player) {
        this.player = player;
    }
}
