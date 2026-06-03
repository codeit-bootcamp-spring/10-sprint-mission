package com.sprint.mission.discodeit.security.registry;

import com.sprint.mission.discodeit.security.jwt.JwtInformation;

import java.util.UUID;

// 토큰 상태 관리 Registry
public interface JwtRegistry {

    // 로그인 성공 시 JwtInformation 등록
    JwtInformation registerJwtInformation(JwtInformation jwtInformation);

    // UserId로 해당 유저의 모든 JwtInformation 정보 삭제
    void invalidateJwtInformationByUserId(UUID userId);

    // JwtInformation이 Registry에 존재하는지 확인
    // UserId를 가진 사용자의 로그인 상태 확인에 사용
    boolean hasActiveJwtInformationByUserId(UUID userId);
    // filter에서 유효한 Access Token 토큰인지 확인에 사용
    boolean hasActiveJwtInformationByAccessToken(String accessToken);
    // 토큰 재발급 시 유효한 Refresh Token인지 확인에 사용
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    // 토큰 재발급 시 토큰 로테이션 수행
    JwtInformation rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    // 만료된 JwtInformation 삭제
    void clearExpiredJwtInformation();
}
