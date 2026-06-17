package com.sprint.mission.discodeit.event;

import java.util.UUID;

// handleBinaryContentCreated에 객체로 묶어서 넘겨야 하므로 record 사용
public record BinaryContentCreatedEvent(UUID binaryContentId, byte[] bytes) {

}
