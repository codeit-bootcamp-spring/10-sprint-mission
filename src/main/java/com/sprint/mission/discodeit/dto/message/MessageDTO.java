package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;

import java.util.UUID;

public record MessageDTO(
        UUID messageId,
        Message message
) {}
