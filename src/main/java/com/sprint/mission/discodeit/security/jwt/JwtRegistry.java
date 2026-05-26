package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

public interface JwtRegistry {

    void registerJwtInformation(JwtInformation jwtInformation);

    void invalidateJwtInformationByRefreshToken(String refreshToken);

    void invalidateJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByUserId(UUID userId);

    boolean hasActiveJwtInformationByAccessToken(String accessToken);

    boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

    void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

    void clearExpiredJwtInformation();

}
