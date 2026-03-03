package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id;
    private final Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private ChannelType type;
    private String channelName;
    private String description;
    private Set<UUID> participantIds;

    private Channel(UUID id) {
        this.id = id;
        this.participantIds = new HashSet<>();
    }

    public Channel(UUID id, List<UUID> participantIds) {
        this(id);
        this.type = ChannelType.PRIVATE;
        this.channelName = null;
        this.description = null;
        this.participantIds = Set.copyOf(participantIds);
    }

    public Channel(UUID id, String channelName, String description) {
        this(id);
        this.type = ChannelType.PUBLIC;
        this.channelName = channelName;
        this.description = description;
    }

    public boolean matchChannelType(ChannelType type) {
        return this.type == type;
    }

    public boolean isPrivateMember(UUID userId) {
        if (type == ChannelType.PUBLIC) {
            return false;
        }
        return participantIds.contains(userId);
    }

    public ChannelResponse toResponse() {
        return ChannelResponse.builder()
                .id(id)
                .name(channelName)
                .description(description)
                .type(type)
                .participantIds(List.copyOf(participantIds))
                .createdAt(TimeConverter.toDateTime(createdAt))
                .updatedAt(TimeConverter.toDateTime(updatedAt))
                .build();
    }

    public void update(String newName, String newDescription) {
        boolean hasUpdated = false;
        hasUpdated |= updateIfChanged(this.channelName, newName, val -> this.channelName = val);
        hasUpdated |= updateIfChanged(this.description, newDescription, val -> this.description = val);

        if (hasUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public boolean isVisibleTo(UUID userId) {
        return this.matchChannelType(ChannelType.PUBLIC) || this.isPrivateMember(userId);
    }
}
