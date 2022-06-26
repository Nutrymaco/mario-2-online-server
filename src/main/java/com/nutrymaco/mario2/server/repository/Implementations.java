package com.nutrymaco.mario2.server.repository;

import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.Produces;
import java.util.*;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
public class Implementations {

    @Singleton
    @Produces
    public RoomRepository roomRepository() {
        return new RoomRepository() {

            private final Map<String, Room> roomByName = new HashMap<>();

            private final Map<String, String> roomNameByPlayerName = new HashMap<>();

            @Override
            public void addRoom(Room room) {
                roomByName.put(room.name(), room);
                for (Player player : room.players()) {
                    roomNameByPlayerName.put(player.name(), room.name());
                }
            }

            @Override
            public Room getRoomByName(String roomName) {
                return requireNonNull(roomByName.get(roomName));
            }

            @Override
            public RoomModificator modifyRoom(String roomName) {
                var room = requireNonNull(roomByName.get(roomName));
                return new RoomModificator() {
                    @Override
                    public void updateName(String newName) {
                        var newRoom = new Room(newName, room.players(), room.level());
                        roomByName.put(newName, newRoom);
                        roomNameByPlayerName.replaceAll((key, value) -> {
                            if (value.equals(roomName)) {
                                return newName;
                            }
                            return roomName;
                        });
                    }

                    @Override
                    public void addPlayer(Player player) {
                        var players = new HashSet<>(room.players());
                        players.add(player);
                        roomByName.put(roomName, new Room(room.name(), players, room.level()));
                        roomNameByPlayerName.put(player.name(), roomName);
                    }

                    @Override
                    public void removePlayer(Player player) {
                        var players = new HashSet<>(room.players());
                        players.remove(player);
                        roomByName.put(roomName, new Room(room.name(), players, room.level()));
                        roomNameByPlayerName.remove(player.name());
                    }
                };
            }

            @Override
            public Room getRoomByPlayerName(String playerName) {
                var roomName = requireNonNull(roomNameByPlayerName.get(playerName));
                return getRoomByName(roomName);
            }

            @Override
            public Collection<Room> getRooms() {
                return roomByName.values();
            }
        };
    }

    @Singleton
    @Produces
    public PlayerRepository userRepository() {
        return new PlayerRepository() {
            private static final Map<String, Player> playerByName = new HashMap<>();

            @Override
            public void addPlayer(Player player) {
                playerByName.put(player.name(), player);
            }

            @Override
            public Player getPlayer(String playerName) {
                return playerByName.get(playerName);
            }

        };
    }
}
