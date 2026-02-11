package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageUpdateDto(
        UUID id,
        String newContent,
        List<UUID> attachmentIds

) {
}
