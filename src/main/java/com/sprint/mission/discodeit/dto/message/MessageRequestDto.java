package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;

import java.util.List;
import java.util.UUID;

public record MessageRequestDto(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentRequestDto> binaryContentRequestDto
) {}
