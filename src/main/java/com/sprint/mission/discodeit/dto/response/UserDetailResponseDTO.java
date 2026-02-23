package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

// find, findAll 반환할 DTO(userStatus의 online정보를 포함)
public record UserDetailResponseDTO(
   UUID id,
   Instant createdAt,
   Instant updatedAt,
   String username,
   String email,
   UUID profileId,
   boolean online
) {}
