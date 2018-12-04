package com.github.fernthedev.packets;

public class PongPacket extends Packet {
    private long time;

    public long getTime() {
        return time;
    }

    public PongPacket() {
       time = System.nanoTime();
    }

}
