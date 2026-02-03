package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {
    private UUID id;
    private Instant createdAt;
    //
    private byte[] bytes; //이진데이터(실제 파일 데이터)
    private String fileName; //파일 이름
    private String contentType; //파일 형식
    private UUID messageId;

    public BinaryContent(byte[] bytes, String fileName, String contentType) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.bytes = bytes;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public void setParentMessage(UUID messageId) {
        this.messageId = messageId;
    }

}
