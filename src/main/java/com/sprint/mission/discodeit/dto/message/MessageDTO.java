package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public record MessageDTO(
        UUID messageId,
        String msg,
        UUID userId,
        UUID channelId,
        List<UUID> attachMentIds
) {}
