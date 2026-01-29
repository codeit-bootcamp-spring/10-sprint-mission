package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;

import java.util.List;
import java.util.UUID;

public record MessageCreateDto(
        String content,
        UUID channelId,
        UUID authorId,
        List<BinaryContentCreateDto> attachments
) {
}
