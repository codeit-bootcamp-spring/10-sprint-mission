package com.sprint.mission.discodeit.event;

import java.util.UUID;

/*
    BinaryContentCreatedEvent
    -------------------------
    데이터베이스에 메타 정보가 정상적으로 저장되었음을 알리는 이벤트
*/
public record BinaryContentCreatedEvent(
        UUID binaryContentId,
        byte[] rawData
) {
}