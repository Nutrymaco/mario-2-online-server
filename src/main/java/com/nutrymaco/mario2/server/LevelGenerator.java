package com.nutrymaco.mario2.server;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

@ApplicationScoped
public class LevelGenerator {

    private static final int DEFAULT_HEIGHT = 150;
    private static final int DEFAULT_WIDTH = 40;
    private static final Random random = new Random();


    public String[] generateLevel() {
        var level = IntStream.range(0, DEFAULT_HEIGHT)
                .mapToObj(ignored -> " ".repeat(DEFAULT_WIDTH))
                .toArray(String[]::new);
        for (int i = 0; i < DEFAULT_HEIGHT; i += 6) {
            int blockSize = random.nextInt(3);
            int blockStart = random.nextInt(0, DEFAULT_WIDTH - blockSize);
            String curLevel = level[i];
            level[i] = curLevel.substring(0, blockStart) + "-".repeat(blockSize) + curLevel.substring(blockStart);
        }
        return level;
    }

}
