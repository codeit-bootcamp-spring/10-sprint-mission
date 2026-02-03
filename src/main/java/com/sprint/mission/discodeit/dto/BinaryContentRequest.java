package com.sprint.mission.discodeit.dto;

import lombok.Getter;

@Getter
public class BinaryContentRequest {
    private final String fileName;
    private final byte[] content;
    private final String contentType;

    public BinaryContentRequest(String fileName, byte[] content, String contentType) {
        this.fileName = fileName;
        this.content = content;
        this.contentType = contentType;
    }
}
