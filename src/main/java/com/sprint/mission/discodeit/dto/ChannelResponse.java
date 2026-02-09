package com.sprint.mission.discodeit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@ToString
public class ChannelResponse {
    private UUID id;
    private String name;
    private String type;
    private String description;
    private List<UUID> participantIds;
    private Instant lastMessageAt;

    public ChannelResponse(UUID id, String name, String type, String description, List<UUID> participantIds, Instant lastMessageAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.participantIds = participantIds;
        this.lastMessageAt = lastMessageAt;
    }
}