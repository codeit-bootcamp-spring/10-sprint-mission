package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateDto(
        UUID userId,
        String newName,
        UUID profileId
) {}
