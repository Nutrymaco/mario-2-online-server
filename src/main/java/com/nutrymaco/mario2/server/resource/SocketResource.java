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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
            var otherPlayerNames = getOtherPlayerNames(playerName).toList();
            sendToPlayers(outMessage, otherPlayerNames);
        } else if (DISABLE_BLOCK.equals(message.type())) {
            long maxLag = lagService.getMaxLagForRoom(registrationService.getPlayerRoom(playerName).name());
            long start = System.currentTimeMillis() + maxLag;
            var outMessage = new BaseOutMessage(playerName, DISABLE_BLOCK, new DisableBlockMessage(start, 2_000));
            var playerNames = getOtherPlayerNames(playerName).toList();
            System.out.println(outMessage);
            sendToPlayers(outMessage, playerNames);
        } else {
            System.out.println("unknown message type");
            var outMessage = new BaseOutMessage(playerName, message.type(), message.data());
            sendToPlayers(outMessage, getOtherPlayerNames(playerName).toList());
        }
    }

    private void sendToPlayers(BaseOutMessage outMessage, List<String> playerNames) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(outMessage);
        sessions.entrySet().stream()
                .filter(entry -> playerNames.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .forEach(sendMessage(message));
    }

    private Stream<String> getOtherPlayerNames(String playerName) {
        return registrationService.getPlayerRoom(playerName).players().stream()
                .map(Player::name)
                .filter(playerToSend -> !playerToSend.equals(playerName));
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
