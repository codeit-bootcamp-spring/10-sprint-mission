package com.sprint.mission.discodeit.config;

import java.io.File;

public class FileStorageConfig {
    public static final String DATA_DIR_PATH = "src/main/java/com/sprint/mission/discodeit/data";

    public static File getDataDirectory() {
        File dataDir = new File(DATA_DIR_PATH);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        return dataDir;
    }
}
