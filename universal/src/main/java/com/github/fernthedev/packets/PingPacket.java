package com.github.fernthedev.packets;

public class PingPacket extends Packet {

    private long time;

    public long getTime() {
        return time;
    }

    public PingPacket() {
        time = System.nanoTime();
    }
}
