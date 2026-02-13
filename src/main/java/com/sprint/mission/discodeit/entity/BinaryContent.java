package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String fileName;
    private final String contentType;
    private final byte[] data;
    private final UUID profileUserId;
    private final UUID messageId;

    public BinaryContent(String fileName, String contentType, byte[] data, UUID profileUserId, UUID messageId) {
        super();
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
        this.profileUserId = profileUserId;
        this.messageId = messageId;
    }
}