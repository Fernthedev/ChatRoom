package com.github.fernthedev.packets.player;

import com.github.fernthedev.packets.Packet;

import java.io.Serializable;

public class AddPlayerPacket extends Packet implements Serializable {

    private static final long serialVersionUID = 1L;

    public int id;
    public String name;

    public AddPlayerPacket(String name, int id) {
        this.name = name;
        this.id = id;
    }
}
