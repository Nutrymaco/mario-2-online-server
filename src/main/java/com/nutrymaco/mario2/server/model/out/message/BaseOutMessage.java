package com.nutrymaco.mario2.server.model.out.message;

import com.nutrymaco.mario2.server.model.in.message.MessageType;

import java.util.UUID;

public record BaseOutMessage(UUID playerId, String type, Object data) {
}
