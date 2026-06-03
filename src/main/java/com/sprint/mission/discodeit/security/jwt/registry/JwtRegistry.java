package com.sprint.mission.discodeit.security.jwt.registry;

import com.sprint.mission.discodeit.security.jwt.JwtInformation;

import java.util.UUID;

public interface JwtRegistry {
    // 새로운 로그인 정보 등록
    void registerJwtInformation(JwtInformation jwtInformation);

    // 특정 사용자 강제 로그아웃
    void invalidateJwtInformationByUserId(UUID userId);

    // 로그인 여부 확인 (사용자 ID)
    boolean hasActiveJwtInformationByUserId(UUID userId);

    // 로그인 정보 유효성 여부 확인 (AccessToken)
    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    // 로그인 정보 유효성 여부 확인 (RefreshToken)
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    // 토큰 로테이션
    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    // 만료된 토큰 정리
    void clearExpiredJwtInformation();
}
