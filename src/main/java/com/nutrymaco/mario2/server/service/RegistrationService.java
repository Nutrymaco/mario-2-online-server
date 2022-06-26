package com.nutrymaco.mario2.server.service;

import com.nutrymaco.mario2.server.model.Room;
import com.nutrymaco.mario2.server.model.Player;

import java.util.Collection;

public interface RegistrationService {

    void addUser(Player player);

    void registerPlayerInRoom(String roomName, Player player);

    void createRoom(String roomName);

    Collection<Room> getRooms();

    Room getPlayerRoom(String playerName);
}
