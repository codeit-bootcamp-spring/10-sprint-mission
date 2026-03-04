package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,

    @JsonProperty("username")
    String userName,

    String email,
    UUID profileId,
    Boolean online
) {

}