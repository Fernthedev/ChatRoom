package com.github.fernthedev.packets;

public class MessagePacket extends Packet {

    private String message;

    public MessagePacket(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
