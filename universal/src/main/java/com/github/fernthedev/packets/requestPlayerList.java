package com.github.fernthedev.packets;

import java.io.Serializable;

public class requestPlayerList extends Packet implements Serializable {
    private static final long serialVersionUID = 1L;

    public String cause;

    public requestPlayerList() {
        this.cause = "Unknown reason.";
    }

    public requestPlayerList(String cause) {
        this.cause = cause;
    }
}
