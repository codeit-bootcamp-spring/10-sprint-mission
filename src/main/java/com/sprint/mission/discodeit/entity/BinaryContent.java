package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends ImmutableEntity {
    private final BinaryContentType contentType;
    private final String fileName;
    private final String url;

    public BinaryContent(BinaryContentType contentType, String fileName, String url) {
        super();
        this.contentType = contentType;
        this.fileName = fileName;
        this.url = url;
    }
}
