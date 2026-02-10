package com.sprint.mission.discodeit.channel.dto;

import com.sprint.mission.discodeit.channel.entity.ChannelType;

import java.util.UUID;

public record ChannelCreatePublicRequest(
        ChannelType type,
        String name,
        String description,
        UUID ownerId
) {
}
