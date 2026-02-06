package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UserSummaryResponseDTO(
        UUID userId,
        String username,
        String email,
        UUID profileId
) {}
