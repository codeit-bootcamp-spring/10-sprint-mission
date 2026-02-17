package com.sprint.mission.discodeit.entity;

public enum BinaryContentType {
    PNG("image/png"),
    JPG("image/jpg");

    private final String mimeType;

    BinaryContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public static BinaryContentType fromMimeType(String mimeType) {
        for (BinaryContentType type : values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported mime type: " + mimeType);
    }
}