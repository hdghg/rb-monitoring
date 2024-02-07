package com.github.hdghg.rbmonitoring.model;

import java.sql.Timestamp;

public class CharacterBonus {

    private int id;
    private String nickname;
    private Timestamp at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Timestamp getAt() {
        return at;
    }

    public void setAt(Timestamp at) {
        this.at = at;
    }
}
