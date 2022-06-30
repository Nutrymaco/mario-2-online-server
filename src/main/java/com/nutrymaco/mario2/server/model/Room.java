package com.nutrymaco.mario2.server.model;

import java.util.Set;

public record Room(String name, Set<Player> players, Level level) {

}
