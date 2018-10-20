package com.github.fernthedev.universal;

import java.io.Serializable;

public class NetPlayer implements Serializable {
    private static final long serialVersionUID = 1L;

    public int id;
    public String name;

    public NetPlayer(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
