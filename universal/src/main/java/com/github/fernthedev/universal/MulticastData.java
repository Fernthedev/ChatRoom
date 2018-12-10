package com.github.fernthedev.universal;

import java.util.ArrayList;
import java.util.List;

public class MulticastData {

    public MulticastData() {}

    public MulticastData(int port, String verison) {
        this.port = port;
        this.version = verison;
    }

    public MulticastData(int port, String verison,int clientNumbers) {
        this(port,verison);
        this.clientNumbers = clientNumbers;
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

    private int clientNumbers = 0;
    private List<String> clients =  new ArrayList<>();

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
