package com.nutrymaco.mario2.server.resource;

import com.nutrymaco.mario2.server.repository.PlayerRepository;
import com.nutrymaco.mario2.server.repository.RoomRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.util.Map;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/players")
@ApplicationScoped
public class PlayerResource {

    @Inject
    PlayerRepository playerRepository;

    @Inject
    RoomRepository roomRepository;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Map<String, Object> addPlayer(Player player) {
        UUID playerId = playerRepository.addPlayer(player.playerName());
        roomRepository.modifyRoom(player.roomId()).addPlayer(playerId);
        return Map.of(
                "id", playerId,
                "room", Map.of(
                        "id", player.roomId()
                )
        );
    }

    public record Player(String playerName, String roomId){}

}
