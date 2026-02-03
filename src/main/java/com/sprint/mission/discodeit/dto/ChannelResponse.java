package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.ToString;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class ChannelResponse {
    private final UUID id;
    private final String name;
    private final String type;
    private final String description;
    private final List<UUID> participantIds;
    private final Instant lastMessageAt;

    public ChannelResponse(UUID id, String name, String type, String description, List<UUID> participantIds, Instant lastMessageAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.participantIds = participantIds;
        this.lastMessageAt = lastMessageAt;
    }
}