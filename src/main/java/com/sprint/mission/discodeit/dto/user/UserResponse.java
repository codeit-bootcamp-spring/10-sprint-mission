package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "유저 응답")
public record UserResponse(
    @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
    UUID id,
    @Schema(description = "유저 생성 시간", example = "2026-02-23T01:30:54Z")
    Instant createdAt,
    @Schema(description = "유저 수정 시간", example = "2026-02-23T01:30:54Z")
    Instant updatedAt,
    @Schema(description = "유저 이름", example = "woody")
    String username,
    @Schema(description = "유저 이메일", example = "woody@codeit.com")
    String email,
    @Schema(description = "유저 프로필 사진 ID", example = "0b71409f-f489-40a2-a075-c2c93640351c")
    UUID profileId,
    @Schema(description = "유저 온라인 상태", example = "true")
    boolean online
) {

  public static UserResponse of(User user, UserStatus status) {
    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        status.isOnline()
    );
  }
}
