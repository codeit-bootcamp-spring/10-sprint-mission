package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보 응답")
public class UserDto {

  @Schema(description = "사용자 고유 ID", example = "550e8400-e29b-41d4-a716-446655440000")
  private UUID id;

  @Schema(description = "생성 일시", example = "2026-02-20T07:23:52Z")
  private Instant createdAt;

  @Schema(description = "수정 일시", example = "2026-02-20T08:23:52Z")
  private Instant updatedAt;

  @Schema(description = "사용자 이름", example = "username")
  private String username;

  @Schema(description = "이메일 주소", example = "test@gmail.com")
  private String email;

  @Schema(description = "프로필 이미지 정보")
  private BinaryContentDto profile;

  @Schema(description = "온라인 접속 상태", example = "true")
  private boolean online;

  @Schema(description = "사용자 권한", example = "USER")
  private Role role;
}
