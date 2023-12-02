package com.github.hdghg.rbmonitoring.model;

import java.sql.Timestamp;

public class Transition {

    private String name;
    private boolean alive;
    private Timestamp at;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Timestamp getAt() {
        return at;
    }

    public void setAt(Timestamp at) {
        this.at = at;
    }
}
