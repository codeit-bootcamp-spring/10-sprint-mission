package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.UserStatusType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserResponseDTO (
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    Instant updatedAt,
    UUID profileId,
    UserStatusType status
){

}