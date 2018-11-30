package com.github.fernthedev.server;

public abstract class ServerCommand {
    private String command;

    private String usage = "";

    public String getCommandName() {
        return command;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public ServerCommand(String command) {
        this.command = command;
    }

    abstract void onCommand(String[] args);
}
