package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
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

    // 파일의 정보를 나타내는 필드
    private final String filePath;
    private final String contentType;

    public BinaryContent(BinaryContentDto.BinaryContentRequest request) {

        this.id = UUID.randomUUID();
        this.createdAt = Instant.ofEpochSecond(System.currentTimeMillis());
        this.filePath = request.filePath();
        this.contentType = request.contentType();

    }
}
