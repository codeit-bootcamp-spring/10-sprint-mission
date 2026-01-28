package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent {                             // 수정 불가능 클래스
    private UUID id;
    private Instant createdTime;                         // 파일 생성 시점
    private byte[] data;                                 // 실제 파일
    private BinaryContentType binaryContentType;         // 파일 종류

    public BinaryContent(BinaryContentType binaryContentType) {
        this.id = UUID.randomUUID();
        this.createdTime = Instant.now();
        this.binaryContentType = binaryContentType;
    }
}