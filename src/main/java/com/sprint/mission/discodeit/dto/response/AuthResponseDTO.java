package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record AuthResponseDTO(
   UUID userId,
   String userName,
   String email,
   UUID profileId
) {}
