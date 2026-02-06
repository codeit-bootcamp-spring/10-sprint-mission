package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends ImmutableEntity {
    private final BinaryContentType contentType;
    private final String fileName;
    private final Byte[] contentBytes;

    public BinaryContent(BinaryContentType contentType, String fileName, Byte[] contentBytes) {
        super();
        this.contentType = contentType;
        this.fileName = fileName;
        this.contentBytes = contentBytes;
    }
}
