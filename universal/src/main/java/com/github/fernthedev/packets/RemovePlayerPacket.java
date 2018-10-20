package com.github.fernthedev.packets;

import com.github.fernthedev.universal.NetPlayer;

import java.io.Serializable;

public class RemovePlayerPacket extends Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id;


    public RemovePlayerPacket(int id) {
        this.id = id;
    }

    public RemovePlayerPacket(NetPlayer player) {
        this.id = player.id;
    }
}
