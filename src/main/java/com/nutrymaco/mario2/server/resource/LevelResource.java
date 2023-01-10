package com.nutrymaco.mario2.server.resource;

import com.nutrymaco.mario2.server.repository.LevelRepository;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.*;

@ApplicationScoped
@Path("/levels")
public class LevelResource {

    @Inject
    LevelRepository levelRepository;

    @POST
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Map<String, UUID> createLevel(MultipartFormDataInput formData) throws IOException {
        File levelFile = formData.getFormDataPart("file", new GenericType<>(File.class));
        String levelData = Files.readString(levelFile.toPath());
        String levelName = formData.getFormDataPart("name", new GenericType<>(String.class));
        Boolean isDefault = formData.getFormDataPart("default", new GenericType<>(boolean.class));
        UUID levelId = levelRepository.addLevel(levelName, levelData, isDefault == null ? false : isDefault);
        return Map.of(
                "id", levelId
        );
    }

    @GET
    @Path("/{levelId}/content")
    @Produces(TEXT_PLAIN)
    public String getLevelData(@PathParam("levelId") UUID levelId) {
        return levelRepository.getLevelData(levelId);
    }

}
