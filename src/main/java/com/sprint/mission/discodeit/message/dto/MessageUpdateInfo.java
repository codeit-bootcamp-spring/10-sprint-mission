package com.sprint.mission.discodeit.message.dto;

import java.util.UUID;

public record MessageUpdateInfo(
        UUID messageId,
        String content
) {}
