package com.sprint.mission.discodeit.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/// 메타 정보가 DB에 잘 저장되었다는 사실을 의미하는 이벤트.
@AllArgsConstructor
@Getter
public class BinaryContentCreatedEvent {
    UUID binaryContentId;
    byte[] bytes;
    UUID userId;

}
