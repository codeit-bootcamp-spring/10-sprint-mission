package com.sprint.mission.discodeit.auth.jwt;

import java.util.UUID;

public interface JwtRegistry {
    void registerJwtInformation(JwtInformation jwtInformation);
    void invalidateJwtInformationByUserId(UUID userId);
    boolean hasActiveJwtInformationByUserId(UUID userId);
    boolean hasActiveJwtInformationByAccessToken(String accessToken);
    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);
    void rotateJwtInformation(String oldRefreshToken, JwtInformation newJwtInformation);
    void clearExpiredJwtInformation();
}
