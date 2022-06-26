package com.nutrymaco.mario2.server.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutrymaco.mario2.server.model.Player;
import com.nutrymaco.mario2.server.model.Room;
import com.nutrymaco.mario2.server.repository.RoomRepository;
import com.nutrymaco.mario2.server.service.RegistrationService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Collection;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationScoped
@Path("")
public class RegistrationResource {

    @Inject
    RegistrationService registrationService;

    @Inject
    RoomRepository roomRepository;

    @Inject
    ObjectMapper objectMapper;

    @PATCH
    @Consumes(APPLICATION_JSON)
    @Path("/rooms/{roomName}")
    public void registerUserInRoom(@PathParam("roomName") String roomName, Player player) {
        if (roomRepository.getRooms().stream().map(Room::name).noneMatch(roomName::equals)) {
            registrationService.createRoom(roomName);
        }
        registrationService.registerPlayerInRoom(roomName, player);
        System.out.println(registrationService.getRooms());
    }

//    @POST
//    @Path("/rooms")
//    public Room createRoom(Room room) {
//        return registrationService.createRoom(room);
//    }

    @GET
    @Path("/rooms")
    public Collection<Room> getRooms() {
        return registrationService.getRooms();
    }

    @GET
    @Path("/rooms/{roomName}/users")
    public Collection<Player> getUsers(@PathParam("roomName") String roomName) {
        return roomRepository.getRoomByName(roomName).players();
    }

}
