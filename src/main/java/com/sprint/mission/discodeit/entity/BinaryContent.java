package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    // 파일, 이미지 등 바이너리 파일
    @Serial
    private static final long serialVersionUID = 1L;
    // updateAt 구현 제외를 위해 상속받지 않고 별도로 구현
    private final UUID id;
    private final Instant createdAt;

    // 의존성 ID 필드
    private final UUID userId;
    private final UUID messageId;

    // 파일의 정보를 나타내는 필드
    private final String fileName;
    private final String fileUrl;
    private final String contentType;

    public BinaryContent(UUID id, Instant createdAt, String fileName, String fileUrl,
                          String contentType, UUID userId, UUID messageId) {

        if ((userId == null) == (messageId == null)) {
            throw new IllegalArgumentException("바이너리 데이터는 유저(프로필) 또는 메시지(첨부파일) 중 정확히 하나에만 귀속되어야 합니다.");
        }

        this.id = id;
        this.createdAt = createdAt;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.contentType = contentType;
        this.userId = userId;
        this.messageId = messageId;
    };
}
