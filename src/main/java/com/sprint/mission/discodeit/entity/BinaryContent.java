package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.type.FileType;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private final UUID id;
    private final UUID userId;
    private final UUID messageId;
    private final Instant createdAt;
    private final byte[] data;
    private final String fileName;
    private final FileType fileType;

    public BinaryContent(UUID userId, UUID messageId, byte[] data, String fileName, FileType fileType){
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.messageId = messageId;
        this.createdAt = Instant.now();
        this.data = data;
        this.fileName = fileName;
        this.fileType = fileType;
    }



}
