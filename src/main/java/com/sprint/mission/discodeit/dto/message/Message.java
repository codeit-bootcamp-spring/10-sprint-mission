package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 클라이언트에게 반환할 메시지 정보
public record Message(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentIds
) {

}
