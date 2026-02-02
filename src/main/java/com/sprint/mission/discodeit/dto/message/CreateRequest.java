package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;

import java.util.List;
import java.util.UUID;

public record CreateRequest(
        String content,
        UUID channelId,
        UUID userID,
        List<BinaryContentCreateRequest> attachments
) {
}
