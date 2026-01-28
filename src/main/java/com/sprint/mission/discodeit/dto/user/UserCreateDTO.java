package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserCreateDTO(
        String email,
        String username,
        String password,
        UUID profileImageId
) {}