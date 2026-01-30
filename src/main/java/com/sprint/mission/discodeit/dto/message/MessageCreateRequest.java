package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;

import java.util.UUID;

public record MessageCreateRequest(
        UUID channelId,
        UUID authorId,
        String content,
        BinaryContentCreateRequest[] attachments
) {
    public MessageCreateRequest {
        if (attachments != null) {
            attachments = attachments.clone();
        }
    }
}
