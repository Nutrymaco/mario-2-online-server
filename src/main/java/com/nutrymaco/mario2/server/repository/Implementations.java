package com.nutrymaco.mario2.server.repository;

import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.Room;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.ws.rs.Produces;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@ApplicationScoped
public class Implementations {

    private static final Random RANDOM = new Random();

    @Singleton
    @Produces
    public RoomRepository roomRepository() {
        return new RoomRepository() {

            private final Map<String, Room> roomById = new HashMap<>();

            @Override
            public String addRoom(Set<UUID> players, UUID levelId) {
                String roomId = generateRoomId();
                roomById.put(roomId, new Room(roomId, players, levelId));
                return roomId;
            }

            @Override
            public RoomModificator modifyRoom(String roomId) {
                var room = requireNonNull(roomById.get(roomId));
                return new RoomModificator() {
                    @Override
                    public void addPlayer(UUID id) {
                        var players = new HashSet<>(requireNonNullElse(room.players(), new HashSet<>()));
                        players.add(id);
                        roomById.put(roomId, new Room(roomId, players, room.levelId()));
                    }
                    @Override
                    public void removePlayer(UUID id) {
                        var players = new HashSet<>(requireNonNullElse(room.players(), new HashSet<>()));
                        players.remove(id);
                        roomById.put(roomId, new Room(roomId, players, room.levelId()));
                    }
                };
            }

            @Override
            public Set<UUID> playersInRoom(String roomId) {
                return roomById.get(roomId).players();
            }

            @Override
            public Collection<?> getRooms() {
                return roomById.values();
            }

            private static String generateRoomId() {
                byte[] rawId = new byte[4];
                for (int i = 0; i < rawId.length; i++) {
                    rawId[i] = (byte) RANDOM.nextInt(65, 90);
                }
                return new String(rawId);
            }

        };
    }

    @Singleton
    @Produces
    public PlayerRepository userRepository() {
        return new PlayerRepository() {
            private static final Map<UUID, Player> playerById = new HashMap<>();

            @Override
            public UUID addPlayer(String playerName) {
                UUID id = UUID.randomUUID();
                playerById.put(id, new Player(playerName));
                return id;
            }

            @Override
            public Player getPlayer(UUID playerId) {
                return playerById.get(playerId);
            }

        };
    }

    @Singleton
    @Produces
    public LevelRepository levelRepository() {
        record Level(String name, String content){}
        return new LevelRepository() {
            private final Map<UUID, Level> levelById = new HashMap<>();
            private UUID defaultLevelId;

            @Override
            public UUID addLevel(String levelName, String levelData, boolean isDefault) {
                UUID levelId = UUID.randomUUID();
                if (isDefault) {
                    defaultLevelId = levelId;
                }
                levelById.put(levelId, new Level(levelName, levelData));
                return levelId;
            }

            @Override
            public UUID getDefaultLevelId() {
                return requireNonNull(defaultLevelId);
            }

            @Override
            public String getLevelData(UUID levelId) {
                return levelById.get(levelId).content();
            }
        };
    }
}
