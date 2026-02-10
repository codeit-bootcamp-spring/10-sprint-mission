package com.sprint.mission.discodeit.dto.request.userStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserStatusCreateRequestDTO (
    @NotNull
    UUID userId
) {

}
