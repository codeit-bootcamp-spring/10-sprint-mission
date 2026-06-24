package com.sprint.mission.discodeit.auth.jwt;

import java.util.Optional;
import java.util.UUID;

/**
 * 서버 측 세션 관리를 위한 JWT 레지스트리 인터페이스입니다.
 * 중복 로그인 방지, 실시간 세션 만료, 온라인 상태 확인을 담당합니다.
 */
public interface JwtRegistry {

  // 새로운 세션(토큰 세트) 등록
  void registerJwtInformation(JwtInformation info);

  // 사용자 ID 기반 세션 만료
  void invalidateJwtInformationByUserId(UUID userId);

  // 리프레시 토큰 기반 세션 만료 (로그아웃 최적화용)
  Optional<UUID> invalidateJwtInformationByRefreshToken(String refreshToken);

  // 사용자 온라인 여부 확인
  boolean hasActiveJwtInformationByUserId(UUID userId);

  // 액세스 토큰 유효 여부 확인 (필터 검사용)
  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  // 리프레시 토큰 유효 여부 확인 (갱신 요청용)
  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  // 기존 세션 정보를 새 정보로 교체 (토큰 로테이션)
  void rotateJwtInformation(String refreshToken, JwtInformation newInfo);

  // 만료된 세션 데이터 정리
  void clearExpiredJwtInformation();
}
