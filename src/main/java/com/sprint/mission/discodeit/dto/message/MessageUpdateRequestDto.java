package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;

import java.util.UUID;

public record MessageUpdateRequestDto(
        UUID messageId,
        String newContent
        ) {
}
