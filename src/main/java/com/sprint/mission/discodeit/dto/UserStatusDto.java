package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {

  @Schema(name = "UserStatusCreateRequest", description = "유저 정보 생성 요청")
  public record Create(
      @Schema(description = "유저 ID", example = "5cd294e0-4cde-4a67-8d5c-3f054927c595")
      UUID userId
  ) {

  }

  @Schema(name = "userStatusResponse", description = "유저 정보 응답")
  public record Response(
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

    public static Response of(UserStatus status) {
      return new Response(
          status.getId(),
          status.getCreatedAt(),
          status.getUpdatedAt(),
          status.getUserId(),
          status.getLastActiveAt()
      );
    }
  }

  @Schema(name = "UserStatusUpdateRequest", description = "새로운 마지막 접속 시각", example = "2026-02-23T01:30:54Z")
  public record Update(
      Instant newLastActiveAt
  ) {

  }
}
