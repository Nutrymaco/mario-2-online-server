package com.nutrymaco.mario2.server.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.in.message.BaseInMessage;
import com.nutrymaco.mario2.server.model.out.message.BaseOutMessage;
import com.nutrymaco.mario2.server.model.out.message.DisableBlockMessage;
import com.nutrymaco.mario2.server.repository.PlayerRepository;
import com.nutrymaco.mario2.server.repository.RoomRepository;
import com.nutrymaco.mario2.server.service.LagService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.nutrymaco.mario2.server.model.in.message.MessageType.*;

@ServerEndpoint("/socket/{playerId}")
@ApplicationScoped
public class SocketResource {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    LagService lagService;

    @Inject
    RoomRepository roomRepository;

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("playerId") String playerId) {
        sessions.put(UUID.fromString(playerId), session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("playerId") String playerId) throws IOException {
        sessions.remove(UUID.fromString(playerId));
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String data, @PathParam("playerId") String playerIdString) throws JsonProcessingException {
        System.out.println("get message + " + data);
        UUID playerId = UUID.fromString(playerIdString);
        var message = objectMapper.readValue(data, BaseInMessage.class);
        lagService.logMessage(message);
        if (POSITION.equals(message.type())) {
            var position = message.data();
            var outMessage = new BaseOutMessage(playerId, POSITION, position);
            var otherPlayerIds = getOtherPlayerIds(message.roomId(), playerId).toList();
            sendToPlayers(outMessage, otherPlayerIds.stream());
        } else if (DISABLE_BLOCK.equals(message.type())) {
            long maxLag = lagService.getMaxLagForRoom(message.roomId());
            long start = System.currentTimeMillis() + maxLag;
            var outMessage = new BaseOutMessage(playerId, DISABLE_BLOCK, new DisableBlockMessage(start, 2_000));
            var playerIds = getOtherPlayerIds(message.roomId(), playerId).toList();
            System.out.println(outMessage);
            sendToPlayers(outMessage, playerIds.stream());
        } else {
            System.out.println("unknown message type");
            var outMessage = new BaseOutMessage(playerId, message.type(), message.data());
            sendToPlayers(outMessage, getOtherPlayerIds(message.roomId(), playerId));
        }
    }

    private void sendToPlayers(BaseOutMessage outMessage, Stream<UUID> playerIds) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(outMessage);
        playerIds.forEach(playerId -> {
            sendMessage(message).accept(sessions.get(playerId));
        });
    }

    private Stream<UUID> getOtherPlayerIds(String roomId, UUID playerId) {
        return roomRepository.playersInRoom(roomId).stream()
                .filter(playerToSend -> !playerToSend.equals(playerId));
    }

    private void broadcast(String message) {
        sessions.values().forEach(sendMessage(message));
    }

    private Consumer<Session> sendMessage(String message) {
        return session -> {
            session.getAsyncRemote().sendObject(message, result -> {
                        if (result.getException() != null) {
                            result.getException().printStackTrace();
                        }
                    }
            );
        };
    }
}
