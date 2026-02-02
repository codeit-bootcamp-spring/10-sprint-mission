package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public record UserUpdateDTO(
        UUID userId,
        String name,
        BinaryContent profile
) {
}
