package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;

public record BinaryContentRequest (
        BinaryContentOwnerType type,
        byte[] image
) {
}
