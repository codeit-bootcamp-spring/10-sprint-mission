package com.sprint.mission.discodeit.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDto(
    UUID id,
    String email,
    String username,
    Instant createdAt,
    Instant updatedAt,
    UUID profileId,
    Boolean online
){

}