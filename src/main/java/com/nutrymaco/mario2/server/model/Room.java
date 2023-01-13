package com.nutrymaco.mario2.server.model;

import java.util.Set;
import java.util.UUID;

public record Room(String roomId, Set<UUID> players, UUID levelId) {

}
