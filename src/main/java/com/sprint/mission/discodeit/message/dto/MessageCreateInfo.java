package com.sprint.mission.discodeit.message.dto;

import java.util.List;
import java.util.UUID;

public record MessageCreateInfo(
        String content,
        UUID senderID,
        UUID channelID,
        List<byte[]> attachments
) {}
