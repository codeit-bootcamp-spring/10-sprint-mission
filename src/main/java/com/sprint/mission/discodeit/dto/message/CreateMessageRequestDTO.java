package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequestDTO(
        String content,
        UUID channelId,
        UUID authorId,
        @Nullable
        List<CreateBinaryContentPayloadDTO> attachments
) { }