package com.nutrymaco.mario2.server.model.in.message;

import java.util.UUID;

public record BaseInMessage(long id, long prevMsgId, UUID playerId, String roomId, long timestamp, String type, Object data) {
}
