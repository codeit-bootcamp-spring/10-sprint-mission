package com.sprint.mission.discodeit.dto.auth;

import java.util.UUID;

public record LoginResponseDTO(
        UUID userId,
        String username,
        String email
) { }
