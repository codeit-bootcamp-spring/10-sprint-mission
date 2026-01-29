package com.sprint.mission.discodeit.dto.messagedto;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

import java.util.List;
import java.util.UUID;

public record MessageUpdateRequestDto(
        UUID messageId,
        String newContent,
        List<BinaryContentDTO> attachmentsToAdd,
        List<UUID> attachmentIdsToRemove
) {
}
