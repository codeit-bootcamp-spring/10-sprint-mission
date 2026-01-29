package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 클라이언트에게 반환할 메시지 정보
public record MessageResponse (
        UUID id,
        String content,
        UUID authorId,
        String authorNickname,
        UUID channelId,
        String channelName,
        boolean isPinned,
        List<UUID> attachmentIds,
        Instant createdAt,
        Instant updatedAt
) {
}
