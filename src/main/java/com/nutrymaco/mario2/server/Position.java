package com.nutrymaco.mario2.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {
    private String playerName;
    private final int x;
    private final int y;

    @JsonCreator
    public Position(
            @JsonProperty(value = "player_name") String playerName,
            @JsonProperty(value = "x") int x,
            @JsonProperty(value = "y") int y) {
        this.playerName = playerName;
        this.x = x;
        this.y = y;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "playerName='" + playerName + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
