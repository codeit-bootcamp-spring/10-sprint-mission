package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserUpdateDto(
        String newName,
        UUID profileId
) {}
