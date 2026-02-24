package com.sprint.mission.discodeit.dto.userstatus;

import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "유저 정보 응답")
public record UserStatusResponse(
    @Schema(description = "유저 정보 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID id,
    @Schema(description = "유저 정보 생성 시간", example = "2026-02-23T01:30:54Z")
    Instant createdAt,
    @Schema(description = "유저 정보 수정 시간", example = "2026-02-23T01:30:54Z")
    Instant updatedAt,
    @Schema(description = "유저 ID", example = "0a56ef3c-4fbb-4b69-96c1-a12d0c3157f5")
    UUID userId,
    @Schema(description = "마지막 접속 시간", example = "2026-02-23T01:30:54Z")
    Instant lastActiveAt
) {

  public static UserStatusResponse of(UserStatus status) {
    return new UserStatusResponse(
        status.getId(),
        status.getCreatedAt(),
        status.getUpdatedAt(),
        status.getUserId(),
        status.getLastActiveAt()
    );
  }
}
