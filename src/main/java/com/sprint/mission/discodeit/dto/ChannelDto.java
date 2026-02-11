package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelDto {
    private UUID id;
    private String name;
    private String description;
    private ChannelType type;
    private Instant lastMessageAt;
    private List<UUID> participantIds;

    public ChannelDto(Channel channel, Instant lastMessageAt, List<UUID> participantIds) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.description = channel.getDescription();
        this.type = channel.getType();
        this.lastMessageAt = lastMessageAt;
        this.participantIds = (participantIds != null) ? participantIds : new ArrayList<>();
    }
}
