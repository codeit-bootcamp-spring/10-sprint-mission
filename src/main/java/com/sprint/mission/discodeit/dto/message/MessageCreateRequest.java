package com.sprint.mission.discodeit.dto.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// 메시지 생성 시 필요한 데이터
public record MessageCreateRequest (
        String content,
        UUID authorId,
        UUID channelId,
        boolean isPinned,
        List<UUID> attachmentIds
) {
}
