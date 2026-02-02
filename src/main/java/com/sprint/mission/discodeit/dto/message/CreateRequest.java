package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public record CreateRequest(
        String content,
        UUID channelId,
        UUID userID,
        List<BinaryContentCreateRequest> attachments
) {
}
