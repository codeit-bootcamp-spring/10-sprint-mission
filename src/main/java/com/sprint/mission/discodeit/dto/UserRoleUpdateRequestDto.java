package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "UserRoleUpdateRequest", description = "사용자 권한 변경 요청")
public record UserRoleUpdateRequestDto(
        @NotNull(message = "사용자 ID는 필수입니다.")
        UUID userId,

        @NotNull(message = "새로운 권한은 필수입니다.")
        Role newRole
) {}
