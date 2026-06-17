package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.exception.common.InvalidInputException;

import java.time.Instant;
import java.util.UUID;

// BinaryContent 메타 데이터가 DB에 저장된 후 실제 Binary 파일 저장을 요청하는 이벤트 클래스
public class BinaryContentCreatedEvent {

    private final UUID binaryContentId;

    private final byte[] bytes;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public BinaryContentCreatedEvent(
            UUID binaryContentId,
            byte[] bytes
    ) {
        if (binaryContentId == null) {
            throw new InvalidInputException("binaryContentId", null);
        }
        if (bytes == null) {
            throw new InvalidInputException("bytes", null);
        }

        this.binaryContentId = binaryContentId;

        // 방어적 복사
        this.bytes = bytes.clone();
        this.occurredAt = Instant.now();
    }

    public UUID getBinaryContentId() {
        return binaryContentId;
    }

    public byte[] getBytes() {
        return bytes.clone();
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
