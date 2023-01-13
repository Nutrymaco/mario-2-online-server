package com.nutrymaco.mario2.server.service;

import com.nutrymaco.mario2.server.model.in.message.BaseInMessage;

import java.util.UUID;

public interface LagService {

    int getLagForUser(UUID playerId);

    int getMaxLagForRoom(String roomId);

    void logMessage(BaseInMessage message);

}
