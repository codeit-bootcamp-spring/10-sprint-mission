package com.sprint.mission.discodeit.channel.dto;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateInfo(
        List<UUID> userIds
) {}
