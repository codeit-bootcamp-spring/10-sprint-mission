package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ToString
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    //
    private ChannelType type;
    private String channelName;
    private String description;
    private Set<UUID> userIdsInPrivateChannel;

    private Channel(UUID id) {
        this.id = id;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = createdAt;
        this.userIdsInPrivateChannel = new HashSet<>();
    }

    public Channel(UUID id, List<UUID> userIdsInPrivateChannel) {
        this(id);
        this.type = ChannelType.PRIVATE;
        this.channelName = null;
        this.description = null;
        this.userIdsInPrivateChannel = Set.copyOf(userIdsInPrivateChannel);
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
        return userIdsInPrivateChannel.contains(userId);
    }

    public ChannelResponse toResponse() {
        // new channel or private channel
        return toResponse(0);
    }

    public ChannelResponse toResponse(long lastMessageTimestamp) {
        return ChannelResponse.builder()
                .channelId(id)
                .channelName(channelName)
                .description(description)
                .type(type)
                .lastMessageTimestamp(lastMessageTimestamp)
                .userIdsInPrivateChannel(List.copyOf(userIdsInPrivateChannel))
                .build();
    }
    // todo: refactoring
    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.channelName)) {
            this.channelName = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }
}
