package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponseDto {
    private final UUID id;
    private final UUID senderId;
    private final UUID channelId;
    private final String content;
    private final Instant createdAt;
    private final List<UUID> attachmentIds;
}
