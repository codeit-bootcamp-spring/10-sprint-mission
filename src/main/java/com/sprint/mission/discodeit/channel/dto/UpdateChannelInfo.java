package com.sprint.mission.discodeit.channel.dto;

import java.util.UUID;

public record UpdateChannelInfo(
        UUID channelId,
        String channelName,
        String description
) {}
