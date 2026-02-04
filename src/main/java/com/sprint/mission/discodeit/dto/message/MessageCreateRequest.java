package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest (
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentRequest> attachments
) {}
