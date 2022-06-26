package com.nutrymaco.mario2.server.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.in.message.BaseInMessage;
import com.nutrymaco.mario2.server.model.out.message.BaseOutMessage;
import com.nutrymaco.mario2.server.model.out.message.DisableBlockMessage;
import com.nutrymaco.mario2.server.service.LagService;
import com.nutrymaco.mario2.server.service.RegistrationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.nutrymaco.mario2.server.model.in.message.MessageType.*;

@ServerEndpoint("/socket/{playerName}")
@ApplicationScoped
public class SocketResource {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    RegistrationService registrationService;

    @Inject
    LagService lagService;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("playerName") String playerName) {
        sessions.put(playerName, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("playerName") String playerName) throws IOException {
        sessions.remove(playerName);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String data, @PathParam("playerName") String playerName) throws JsonProcessingException {
        System.out.println("get message + " + data);
        var message = objectMapper.readValue(data, BaseInMessage.class);
        lagService.logMessage(message);
        if (POSITION.equals(message.type())) {
            var position = message.data();
            var outMessage = new BaseOutMessage(playerName, POSITION, position);
            var room = registrationService.getPlayerRoom(playerName);
            var otherPlayerNames = room.players().stream()
                    .map(Player::name)
                    .filter(name -> !playerName.equals(name))
                    .toList();
            sessions.entrySet().stream()
                    .filter(entry -> otherPlayerNames.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .forEach(sendMessage(objectMapper.writeValueAsString(outMessage)));
        } else if (DISABLE_BLOCK.equals(message.type())) {
            long maxLag = lagService.getMaxLagForRoom(registrationService.getPlayerRoom(playerName).name());
            long start = System.currentTimeMillis() + maxLag;
            var outMessage = new BaseOutMessage(playerName, DISABLE_BLOCK, new DisableBlockMessage(start, 2_000));
            System.out.println(outMessage);
            broadcast(objectMapper.writeValueAsString(outMessage));
        } else {
            throw new IllegalArgumentException();
        }
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
