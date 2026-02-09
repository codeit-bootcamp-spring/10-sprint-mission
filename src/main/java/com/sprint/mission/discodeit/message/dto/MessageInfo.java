package com.sprint.mission.discodeit.message.dto;

import java.util.List;
import java.util.UUID;

public record MessageInfo(
        UUID messageId,
        String content,
        UUID senderID,
        UUID channelID,
        List<UUID> attachmentIds
) {}
