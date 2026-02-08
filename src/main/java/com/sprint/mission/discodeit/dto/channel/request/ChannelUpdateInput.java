package com.sprint.mission.discodeit.dto.channel.request;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ChannelUpdateInput(
        @NotNull(message = "ID가 null입니다.")
        UUID channelId,
        UUID ownerId,
        String channelName,
        String channelDescription
) {
}
