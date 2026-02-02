package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageUpdateInfo(
        UUID messageId,
        String content
) {}
