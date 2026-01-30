package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public class BinaryContent extends ImmutableEntity {
    private final BinaryContent contentType;
    private final Byte[] bytes;

    public BinaryContent(BinaryContent contentType, Byte[] bytes) {
        super();
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
