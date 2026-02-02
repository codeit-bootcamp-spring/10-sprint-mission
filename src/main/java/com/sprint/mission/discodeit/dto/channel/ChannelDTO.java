package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.UUID;

public record ChannelDTO(
        UUID channelId,
        Channel channel
) {}
