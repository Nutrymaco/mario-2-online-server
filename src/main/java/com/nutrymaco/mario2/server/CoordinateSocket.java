package com.nutrymaco.mario2.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.annotations.Pos;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/position/{username}")
@ApplicationScoped
public class CoordinateSocket {

    @Inject
    ObjectMapper objectMapper;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.put(username, session);
        System.out.println("User : " + username + " connected");
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) throws IOException {
        sessions.remove(username);
        System.out.println("User : " + username + " disconnected");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) throws JsonProcessingException {
        var position = objectMapper.readValue(message, Position.class);
        System.out.println("Message : " + position + " from user : " + username);
        position.setPlayerName(username);
        broadcast(objectMapper.writeValueAsString(position));
    }

    private void broadcast(String message) {
        sessions.values().forEach(session -> {
            System.out.println(session.getRequestURI());
            session.getAsyncRemote().sendObject(message, result -> {
                        System.out.println("dis result : " + result.isOK());
                        if (result.getException() != null) {
                            result.getException().printStackTrace();
                        }
                    }
            );
        });
    }

}
