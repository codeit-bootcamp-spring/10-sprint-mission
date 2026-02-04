package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity{
    private final byte[] bytes;
    private final String contentType; // 예: image/png, application/pdf
    private final String fileName;

    public BinaryContent(byte[] bytes, String contentType, String fileName) {
        super();
        this.bytes = bytes;
        this.contentType = contentType;
        this.fileName = fileName;
    }
}
