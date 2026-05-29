package com.sprint.mission.discodeit.security.jwt;

import java.util.UUID;

public interface JwtRegistry {

	void registerJwtInformation(JwtInformation jwtInformation);

	void invalidateJwtInformationByUserId(UUID userId);

	void invalidateJwtInformationByRefreshToken(String refreshToken);

	boolean hasActiveJwtInformationByUserId(UUID userId);

	boolean hasActiveJwtInformationByAccessToken(String accessToken);

	boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

	boolean rotateJwtInformation(String refreshToken, JwtInformation jwtInformation);

	void clearExpiredJwtInformation();
}
