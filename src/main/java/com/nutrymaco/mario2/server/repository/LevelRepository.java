package com.nutrymaco.mario2.server.repository;

import java.util.UUID;

public interface LevelRepository {

    UUID addLevel(String levelName, String levelData, boolean isDefault);

    UUID getDefaultLevelId();

    String getLevelData(UUID levelId);

}
