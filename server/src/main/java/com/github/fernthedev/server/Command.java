package com.github.fernthedev.server;

import org.jetbrains.annotations.NotNull;

public abstract class Command {
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

    public Command(@NotNull String command) {
        this.command = command;
    }

    abstract void onCommand(CommandSender sender,String[] args);
}
