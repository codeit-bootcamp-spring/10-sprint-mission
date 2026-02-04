package com.sprint.mission.discodeit.dto.auth;

import java.util.Optional;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        String userName,
        String email,
        Optional<UUID> profileImageId
) {}
