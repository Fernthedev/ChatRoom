package com.github.fernthedev.packets;

import com.github.fernthedev.universal.NetPlayer;

import java.io.Serializable;
import java.util.HashMap;

public class SendPlayerListPacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    public HashMap<Integer,NetPlayer> players = new HashMap<Integer, NetPlayer>();

    public SendPlayerListPacket(HashMap<Integer,NetPlayer> players) {
        this.players = players;
    }

}
