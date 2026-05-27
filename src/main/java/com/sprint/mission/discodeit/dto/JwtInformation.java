package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT 상태 관리 정보 (서버 레지스트리 저장용)")
public class JwtInformation {

  @Schema(description = "사용자 고유 ID")
  private UUID userId;

  @Schema(description = "인증용 Access Token", example = "eyJhbGciOiJIUzI1NiJ9")
  private String accessToken;

  @Schema(description = "갱신용 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
  private String refreshToken;

  @Schema(description = "토큰 만료 시간", example = "2026-05-26T15:30:00")
  private LocalDateTime expiredAt;

  @Schema(description = "토큰 만료 여부 판단")
  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiredAt);
  }
}
