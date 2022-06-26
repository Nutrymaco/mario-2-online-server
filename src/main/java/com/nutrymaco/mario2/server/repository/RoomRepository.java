package com.nutrymaco.mario2.server.repository;

import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.Room;

import java.util.Collection;

public interface RoomRepository {

    void addRoom(Room room);

    Room getRoomByName(String roomName);

    RoomModificator modifyRoom(String roomName);

    Room getRoomByPlayerName(String playerName);

    Collection<Room> getRooms();

    interface RoomModificator {

        void updateName(String newName);

        void addPlayer(Player player);

        void removePlayer(Player player);

    }

}
