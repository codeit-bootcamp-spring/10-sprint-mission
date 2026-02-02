package com.sprint.mission.discodeit.dto.message;

import java.util.UUID;

public record MessageUpdateDTO(
        UUID messageId,
        String msg
) {}
