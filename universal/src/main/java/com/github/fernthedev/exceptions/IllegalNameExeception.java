package com.github.fernthedev.exceptions;

public class IllegalNameExeception extends Exception {

    private String name;

    public IllegalNameExeception(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
