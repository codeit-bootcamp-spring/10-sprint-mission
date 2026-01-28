package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {

    private UUID id;
    private Instant createdAt;
    //
    //User,Message 객체로 안받고 id로 받는 이유는 결합도 낮추기 위해.
    private UUID userId; //사용자 프로필 이미지용
    private UUID messageId; //메시지에 파일 첨부용
    private byte[] data;
    private String contentType;
    private String fileName;

    public BinaryContent(UUID userId,UUID messageId,byte[] data, String contentType, String fileName) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.messageId = messageId;
        this.data = data;
        this.contentType = contentType;
        this.fileName = fileName;
    }


}
