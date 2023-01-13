package com.nutrymaco.mario2.server.resource;

import com.nutrymaco.mario2.server.repository.LevelRepository;
import com.nutrymaco.mario2.server.repository.RoomRepository;
import org.jboss.resteasy.annotations.Body;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/rooms")
@ApplicationScoped
public class RoomResource {

    @Inject
    RoomRepository roomRepository;

    @Inject
    LevelRepository levelRepository;

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(TEXT_PLAIN)
    public String addRoom(Room room) {
        UUID levelId = room.levelId();
        if (levelId == null) {
            levelId = levelRepository.getDefaultLevelId();
        }
        String roomId = roomRepository.addRoom(room.players(), levelId);
        return roomId;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Object getRooms() {
        return roomRepository.getRooms();
    }

    public record Room(Set<UUID> players, UUID levelId) {}

}
