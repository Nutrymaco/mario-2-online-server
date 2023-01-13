package com.nutrymaco.mario2.server.service;

import com.nutrymaco.mario2.server.model.in.message.BaseInMessage;
import com.nutrymaco.mario2.server.repository.RoomRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class Implementations {

    @Inject
    RoomRepository roomRepository;

    @Singleton
    @Produces
    public LagService lagService() {
        return new LagService() {

            private final Map<UUID, Integer> avgLagByPlayerId = new HashMap<>();

            @Override
            public int getLagForUser(UUID playerId) {
                return avgLagByPlayerId.get(playerId);
            }

            @Override
            public int getMaxLagForRoom(String roomId) {
                var players = roomRepository.playersInRoom(roomId);

                return avgLagByPlayerId.entrySet().stream()
                        .filter(entry -> players.contains(entry.getKey()))
                        .mapToInt(Map.Entry::getValue)
                        .max()
                        .orElse(0);

            }

            // todo: support timezones
            @Override
            public void logMessage(BaseInMessage message) {
                int curLag = (int) (System.currentTimeMillis() - message.timestamp());
                Integer oldLag = avgLagByPlayerId.get(message.playerId());
                int newLag;
                if (oldLag == null) {
                    newLag = curLag;
                } else {
                    newLag = (int) (oldLag * 0.4 + curLag * 0.6);
                }
                avgLagByPlayerId.put(message.playerId(), newLag);
            }
        };
    }

}
