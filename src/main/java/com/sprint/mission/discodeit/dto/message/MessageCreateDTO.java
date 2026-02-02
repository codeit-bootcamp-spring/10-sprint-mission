package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageCreateDTO(
        String msg,
        UUID channelId,
        UUID userId,
        List<UUID> attachmentIds
) {}
