package com.sprint.mission.discodeit.message.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public record MessageUpdateRequest(
        UUID messageId,
        String content,
        List<BinaryContentResponse> attachments

) {
}
