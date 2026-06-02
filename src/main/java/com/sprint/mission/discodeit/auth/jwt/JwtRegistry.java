package com.sprint.mission.discodeit.auth.jwt;

import java.util.UUID;

public interface JwtRegistry {

  // 1. 토큰 등록
  void registerJwtInformation(JwtInformation info);

  // 2. 해당 유저의 토큰 삭제 (로그아웃)
  void invalidateJwtInformationByUserId(UUID userId);

  // 3. 온라인 상태 판단
  boolean hasActiveJwtInformationByUserId(UUID userId);

  // 4. Access Token 검증 (필터용)
  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  // 5. Refresh Token 검증 (재발급용)
  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  // 6. 토큰 로테이션
  void rotateJwtInformation(String refreshToken, JwtInformation newInfo);

  // 7. 만료된 토큰 정리
  void clearExpiredJwtInformation();
}
