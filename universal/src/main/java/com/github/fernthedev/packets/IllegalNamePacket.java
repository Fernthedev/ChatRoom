package com.github.fernthedev.packets;

public class IllegalNamePacket extends Packet {

    private String name;
    private String message;

    public IllegalNamePacket(String name,String message) {
        this.name = name;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}
