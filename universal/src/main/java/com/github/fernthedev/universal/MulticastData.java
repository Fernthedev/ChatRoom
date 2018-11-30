package com.github.fernthedev.universal;

import java.util.List;

public class MulticastData {

    public MulticastData() {}

    public MulticastData(int port, String verison) {
        this.port = port;
        this.version = verison;
    }

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String version;
    private int port;

    private int clientNumbers;
    private List<String> clients;

    public String getVersion() {
        return version;
    }

    public int getPort() {
        return port;
    }

    public int getClientNumbers() {
        return clientNumbers;
    }

    public List<String> getClients() {
        return clients;
    }
}
