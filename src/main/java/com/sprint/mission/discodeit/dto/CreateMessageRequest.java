package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest (
        UUID channelId,
        String content,
        List<BinaryContentRequest> attachments
) {
}
