package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface ChannelServiceDTO {
    record ChannelCreation(@NonNull ChannelType type, @NonNull String channelName, @NonNull String description,
                           List<UUID> userIdsInChannel) {
    }

    record ChannelInfoUpdate(@NonNull UUID channelId, String newName, String newDescription) {
    }

    @Builder
    record ChannelResponse(UUID channelId, String channelName, String description, ChannelType type,
                           long lastMessageTime, List<UUID> joinedUserIds) {
    }
}
