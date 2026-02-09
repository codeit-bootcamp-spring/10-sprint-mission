package com.sprint.mission.discodeit.dto.channel.request;

import java.util.List;
import java.util.UUID;

// Private Channel은 name, descriptions 없어야 함
public record ChannelCreateRequestPrivate(
        List<UUID> userIds
) {}
