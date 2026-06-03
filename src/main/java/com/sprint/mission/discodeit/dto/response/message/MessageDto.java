package com.sprint.mission.discodeit.dto.response.message;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Builder
public record MessageDto (
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        String content,
        UUID channelId,
        UserDto author,
        List<BinaryContentDto> attachments
) {
}
