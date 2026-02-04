package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * BinaryContent
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델. 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용.
 * 불변 객체
 */

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    // 수정 불가능한 도메인 모델 -> updatedAt 필드 정의하지 않음

    private final String fileName;
    private final String contentType;  // 파일 타입 (png, jpg)
    private final long size;  // 파일 크기
    private final byte[] bytes;  // 실제 바이너리 데이터

    public BinaryContent(String fileName, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
        this.size = bytes != null ? bytes.length : 0;
    }
}
