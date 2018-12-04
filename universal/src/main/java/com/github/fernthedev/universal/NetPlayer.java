package com.github.fernthedev.universal;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class NetPlayer implements Serializable {
    private static final long serialVersionUID = -88331240093021207L;

    public int id;
    public String name;

    public long ping;



    public NetPlayer(int id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }
}
