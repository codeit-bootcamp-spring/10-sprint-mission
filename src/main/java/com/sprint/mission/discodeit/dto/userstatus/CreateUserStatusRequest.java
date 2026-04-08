package com.sprint.mission.discodeit.dto.userstatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(description = "User 상태 생성 정보")
public record CreateUserStatusRequest(
        @Schema(description = "상태를 생성할 User ID", example = "d2d837b7-acb8-4e6c-87ad-0f5841aa96b9")
        @NotNull(message = "userId는 필수입니다.")
        UUID userId
) {
}
