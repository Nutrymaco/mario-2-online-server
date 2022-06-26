package com.nutrymaco.mario2.server.service;

import com.nutrymaco.mario2.server.LevelGenerator;
import com.nutrymaco.mario2.server.model.Level;
import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.Room;
import com.nutrymaco.mario2.server.model.in.message.BaseInMessage;
import com.nutrymaco.mario2.server.repository.RoomRepository;
import com.nutrymaco.mario2.server.repository.PlayerRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Produces;
import java.util.*;

import static java.util.Objects.requireNonNull;

@ApplicationScoped
public class Implementations {

    @Inject
    RoomRepository roomRepository;

    @Inject
    PlayerRepository playerRepository;

    @Inject
    LevelGenerator levelGenerator;

    @Produces
    @Singleton
    public RegistrationService registrationService() {
        return new RegistrationService() {

            @Override
            public void addUser(Player player) {
                playerRepository.addPlayer(player);
            }

            @Override
            public void registerPlayerInRoom(String roomName, Player player) {
                roomRepository.modifyRoom(roomName).addPlayer(player);
            }

            @Override
            public void createRoom(String roomName) {
                var level = new Level(levelGenerator.generateLevel());
                var room = new Room(roomName, new HashSet<>(), level);
                roomRepository.addRoom(room);
            }

            @Override
            public Collection<Room> getRooms() {
                return roomRepository.getRooms();
            }

            @Override
            public Room getPlayerRoom(String playerName) {
                return requireNonNull(roomRepository.getRoomByPlayerName(playerName));
            }
        };
    }

    @Singleton
    @Produces
    public LagService lagService() {
        return new LagService() {

            private final Map<String, Integer> avgLagByPlayerName = new HashMap<>();

            @Override
            public int getLagForUser(String playerName) {
                return avgLagByPlayerName.get(playerName);
            }

            @Override
            public int getMaxLagForRoom(String roomName) {
                var playerNames = roomRepository.getRoomByName(roomName).players().stream()
                        .map(Player::name)
                        .toList();

                return avgLagByPlayerName.entrySet().stream()
                        .filter(entry -> playerNames.contains(entry.getKey()))
                        .mapToInt(Map.Entry::getValue)
                        .max()
                        .orElse(0);

            }

            // todo: support timezones
            @Override
            public void logMessage(BaseInMessage message) {
                int curLag = (int) (System.currentTimeMillis() - message.timestamp());
                Integer oldLag = avgLagByPlayerName.get(message.playerName());
                int newLag;
                if (oldLag == null) {
                    newLag = curLag;
                } else {
                    newLag = (int) (oldLag * 0.4 + curLag * 0.6);
                }
                avgLagByPlayerName.put(message.playerName(), newLag);
            }
        };
    }

}
