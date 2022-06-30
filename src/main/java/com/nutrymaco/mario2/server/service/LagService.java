package com.nutrymaco.mario2.server.service;

import com.nutrymaco.mario2.server.model.in.message.BaseInMessage;

public interface LagService {

    int getLagForUser(String playerName);

    int getMaxLagForRoom(String roomName);

    void logMessage(BaseInMessage message);

}
