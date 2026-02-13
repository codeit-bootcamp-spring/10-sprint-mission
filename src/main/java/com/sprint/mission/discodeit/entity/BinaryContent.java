package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델로,
 * 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용 <br>
 * 수정 불가능한 도메인 모델로 간주 -> `updatedAt` 필드 정의하지 않음 <br>
 * `User`, `Message` 도메인 모델과의 의존 관계 방향성 고려하여 `id` 참조 필드를 추가
 */
@Getter
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final String contentType;
    private final byte[] bytes;

    // 생성자
    public BinaryContent(String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.contentType = contentType;
        this.bytes = bytes.clone();
    }

    // getter
    public byte[] getBytes() {
        return bytes.clone();
    }
}
