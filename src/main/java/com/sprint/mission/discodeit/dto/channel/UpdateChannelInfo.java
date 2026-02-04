package com.sprint.mission.discodeit.dto.channel;

import java.util.UUID;

public record UpdateChannelInfo(
        UUID channelId,
        String channelName,
        String description
) {}
