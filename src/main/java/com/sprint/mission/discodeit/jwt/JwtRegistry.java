package com.sprint.mission.discodeit.jwt;

import java.util.UUID;

public interface JwtRegistry {

    void registerJwtInformation(JwtInformation jwtInformation);

    void invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String accessToken, String refreshToken);

    void clearExpiredJwtInformation();
}
