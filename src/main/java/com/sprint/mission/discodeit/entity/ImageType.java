package com.sprint.mission.discodeit.entity;

import java.util.Arrays;

public enum ImageType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif"),
    WEBP("image/webp");

    private final String fileType;

    ImageType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public static boolean isAllowed(String fileType) {
        return Arrays.stream(values())
                .anyMatch(type -> type.fileType.equalsIgnoreCase(fileType));
    }
}
