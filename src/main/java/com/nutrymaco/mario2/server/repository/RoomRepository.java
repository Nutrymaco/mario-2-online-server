package com.nutrymaco.mario2.server.repository;

import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.Room;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface RoomRepository {

    String addRoom(Set<UUID> players, UUID levelId);

    RoomModificator modifyRoom(String roomId);

    Set<UUID> playersInRoom(String roomId);

    Collection<?> getRooms();

    interface RoomModificator {

        void addPlayer(UUID playerId);

        void removePlayer(UUID playerId);

    }

}
