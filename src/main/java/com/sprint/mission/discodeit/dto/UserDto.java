package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

public class UserDto {

  @Schema(name = "userCreateRequest", description = "유저 생성 요청")
  public record Create(
      @NotBlank
      @Schema(description = "유저 이름", example = "woody")
      String username,
      @Email
      @Schema(description = "유저 이메일", example = "woody@codeit.com")
      String email,
      @Schema(description = "유저 비밀번호", example = "1234")
      String password
  ) {

  }

  @Schema(name = "userResponse", description = "유저 응답")
  public record Response(
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

    public static Response of(User user, UserStatus status) {
      return new Response(
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

  @Schema(name = "userUpdateRequest", description = "유저 수정 요청")
  public record Update(
      @Schema(description = "새로운 유저 이름", example = "buzz")
      String newUsername,
      @Schema(description = "새로운 유저 이메일", example = "buzz@codeit.com")
      String newEmail,
      @Schema(description = "새로운 유저 비밀번호", example = "1234")
      String newPassword
  ) {

  }

  @Schema(name = "loginRequest", description = "유저 로그인 요청")
  public record Login(
      @Schema(description = "유저 이름", example = "woody")
      String username,
      @Schema(description = "유저 비밀번호", example = "1234")
      String password
  ) {

  }
}
