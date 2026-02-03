package com.sprint.mission.discodeit.dto.channel;

import java.util.Set;
import java.util.UUID;

public record CreatePrivateChannelRequest(
        Set<UUID> memberIds
) {
}
