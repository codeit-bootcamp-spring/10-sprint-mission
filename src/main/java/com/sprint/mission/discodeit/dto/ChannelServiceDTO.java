package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.NonNull;

import java.util.UUID;

public interface ChannelServiceDTO {
    record ChannelCreation(@NonNull ChannelType type, @NonNull String name, @NonNull String description) {}
    record ChannelInfoUpdate(@NonNull UUID channelId, String newName, String newDescription) {}
}
