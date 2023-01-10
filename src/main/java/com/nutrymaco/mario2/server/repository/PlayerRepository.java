package com.nutrymaco.mario2.server.repository;

import com.nutrymaco.mario2.server.model.Player;

import java.util.UUID;

public interface PlayerRepository {

    UUID addPlayer(String playerName);

    Player getPlayer(UUID playerId);

}
