package com.nutrymaco.mario2.server.repository;

import com.nutrymaco.mario2.server.model.Player;

public interface PlayerRepository {

    void addPlayer(Player player);

    Player getPlayer(String playerName);

}
