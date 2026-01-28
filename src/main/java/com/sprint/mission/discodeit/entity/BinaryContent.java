package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createAt;
    //
    private String fileName;
    private String contentType;
    private byte[] content;
    private long size;

    public BinaryContent(String fileName, String contentType, byte[] content) {
        this.id = UUID.randomUUID();
        this.createAt = Instant.now();
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
        this.size = (content != null) ? content.length : 0L;
    }

}
