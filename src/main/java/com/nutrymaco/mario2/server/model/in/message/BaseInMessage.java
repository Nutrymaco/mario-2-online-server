package com.nutrymaco.mario2.server.model.in.message;

public record BaseInMessage(long id,long prevMsgId, String playerName, long timestamp, MessageType type, Object data) {
}
