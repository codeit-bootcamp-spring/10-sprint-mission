package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequest (
        UUID channelId,
        String content,
        List<BinaryContentRequest> attachments
) {
}
