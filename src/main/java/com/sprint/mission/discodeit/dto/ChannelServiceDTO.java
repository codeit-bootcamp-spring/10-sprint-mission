package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public interface ChannelServiceDTO {
    record ChannelCreateRequest(@NonNull ChannelType type, String channelName, String description,
                                List<UUID> userIdsInChannel) {
    }

    record PublicChannelCreateRequest(@NonNull String channelName, @NonNull String description) {
    }

    record PrivateChannelCreateRequest(@NonNull List<UUID> userIdsInPrivateChannel) {
    }

    record PublicChannelUpdateRequest(@NonNull UUID channelId, String newName, String newDescription) {
    }

    @Builder
    record ChannelResponse(@NonNull UUID channelId, String channelName, String description, @NonNull ChannelType type,
                           long lastMessageTimestamp, List<UUID> userIdsInPrivateChannel) {
        public ChannelResponse {
            if (type == ChannelType.PUBLIC) {
                Objects.requireNonNull(channelName);
                Objects.requireNonNull(description);
            }
        }
    }
}
