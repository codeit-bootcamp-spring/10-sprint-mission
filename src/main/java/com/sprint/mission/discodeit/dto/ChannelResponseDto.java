package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponseDto {
    private final UUID id;
    private final String name;
    private final String description;
    private final ChannelType type;
    private final Instant lastMessageAt;
    private final List<UUID> memberIds;
}
