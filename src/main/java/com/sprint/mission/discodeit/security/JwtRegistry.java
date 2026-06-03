package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.auth.JwtInformation;
import java.util.UUID;

public interface JwtRegistry {

  // 로그인 성공 시 토큰 정보 등록
  void registerJwtInformation(JwtInformation jwtInformation);

  // 특정 유저의 모든 토큰 정보를 무효화
  void invalidateJwtInformationByUserId(UUID userId);

  // 상태 확인 (유저 ID 기준 - 현재 로그인 상태인지 파악)
  boolean hasActiveJwtInformationByUserId(UUID userId);

  // 상태 확인 (Access Token 기준 - 필터에서 유효성 검사 시)
  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  // 상태 확인 (Refresh Token 기준 - 재발급 시 유효성 검사)
  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  // 토큰 로테이션
  void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

  // 만료된 토큰 청소
  void clearExpiredJwtInformation();
}
