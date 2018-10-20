package com.github.fernthedev.packets;

import java.io.Serializable;

public class requestPlayerClassPacket extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    public String cause;

    public requestPlayerClassPacket(String cause) {
       this.cause = cause;
    }

    public requestPlayerClassPacket() {
        this.cause = "Most likely null name.";
    }
}
