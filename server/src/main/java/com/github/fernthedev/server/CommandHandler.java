package com.github.fernthedev.server;

public class CommandHandler implements Runnable {

    private final ServerCommand serverCommand;
    private final String[] args;

    public CommandHandler(ServerCommand command, String[] args) {
        this.serverCommand = command;
        this.args = args;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        serverCommand.onCommand(args);
    }
}
