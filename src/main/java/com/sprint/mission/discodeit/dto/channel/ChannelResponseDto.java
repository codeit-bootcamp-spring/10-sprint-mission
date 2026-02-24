package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusRequestDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponseDto(
        String name,
        String description,
        Instant lastMessageAt,
        List<UUID> userId

) {
}
