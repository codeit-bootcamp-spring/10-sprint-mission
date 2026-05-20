package com.sprint.mission.discodeit.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleUpdateRequest(
    @NotNull
    UUID userId,
    @NotNull
    // 프론트 요청 필드명(newRole)에 맞춰 role로 매핑
    @JsonProperty("newRole")
    UserRole role
) {

}
