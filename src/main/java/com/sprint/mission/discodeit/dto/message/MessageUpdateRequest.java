package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

// 메시지 수정 시 필요한 데이터
public record MessageUpdateRequest (
        String content,
        List<UUID> attachmentIds
) {
}
