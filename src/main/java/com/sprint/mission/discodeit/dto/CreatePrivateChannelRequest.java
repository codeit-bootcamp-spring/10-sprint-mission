package com.sprint.mission.discodeit.dto;

import java.util.Set;
import java.util.UUID;

public record CreatePrivateChannelRequest(
        Set<UUID> memberIds
) {
}
