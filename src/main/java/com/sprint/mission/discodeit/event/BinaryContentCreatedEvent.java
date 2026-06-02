package com.sprint.mission.discodeit.event;

import java.util.UUID;

// BinaryContent 메타데이터 DB 저장 이벤트
public record BinaryContentCreatedEvent(
    UUID binaryContentId,
    byte[] bytes
) {

}
