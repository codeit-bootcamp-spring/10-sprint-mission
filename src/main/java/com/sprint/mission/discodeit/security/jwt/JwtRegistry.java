package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

public interface JwtRegistry {

  // 로그인 성공 시 호출
  // JwtInformation을 Registry에 등록하고 동시 로그인 수 제어
  // maxActiveJwtCount 초과 시 가장 오래된 세션을 자동으로 제거
  void registerJwtInformation(JwtInformation info);

  // 특정 유저의 모든 JwtInformation을 삭제
  // 사용 시점: 권한 변경, 관리자 강제 로그아웃
  void invalidateJwtInformationByUserId(UUID userId);

  // userId 기준으로 Registry에 활성 JwtInformation이 있는지 확인
  // 사용 시점: 사용자의 현재 로그인 여부 판단
  boolean hasActiveJwtInformationByUserId(UUID userId);

  // Access Token 기준으로 Registry에 활성 JwtInformation이 있는지 확인
  // 사용 시점: JwtAuthenticationFilter에서 매 요청마다 토큰 유효성 검사
  // 서명/만료 검사(JwtTokenProvider) + Registry 존재 확인 둘 다 통과해야 인증 완료
  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  // Refresh Token 기준으로 Registry에 활성 JwtInformation이 있는지 확인
  // 사용 시점: POST /api/auth/refresh 에서 재발급 전 유효성 확인
  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  // Token Rotation 수행
  // oldRefreshToken에 해당하는 기존 JwtInformation을 찾아 newInfo로 교체
  // 사용 시점: POST /api/auth/refresh 에서 새 토큰 발급 시
  void rotateJwtInformation(String oldRefreshToken, JwtInformation newInfo);

  // 만료된 JwtInformation을 Registry에서 제거
  // 사용 시점: @Scheduled(fixedDelay = 1000 * 60 * 5)로 5분마다 자동 호출
  // 목적: 메모리 누수 방지
  void clearExpiredJwtInformation();
}
