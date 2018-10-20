package com.github.fernthedev.packets;

public class PongPacket extends Packet {
    private long time;

    public long getTime() {
        return System.nanoTime() - time;
    }

    public PongPacket() {
       time = System.nanoTime();
    }

}
