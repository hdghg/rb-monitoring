package com.github.hdghg.rbmonitoring.model;

import java.sql.Timestamp;

public class CharacterBonus {

    private String nickname;
    private Timestamp at;

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
