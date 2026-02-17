package com.sprint.mission.discodeit.message.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentResponse> attachments
) {
}
