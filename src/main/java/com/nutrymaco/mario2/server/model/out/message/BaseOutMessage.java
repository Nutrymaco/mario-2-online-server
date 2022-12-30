package com.nutrymaco.mario2.server.model.out.message;

import com.nutrymaco.mario2.server.model.in.message.MessageType;

public record BaseOutMessage(String playerName, String type, Object data) {
}
