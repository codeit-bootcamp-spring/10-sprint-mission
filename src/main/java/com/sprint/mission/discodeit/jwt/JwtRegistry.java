package com.sprint.mission.discodeit.jwt;

import java.util.UUID;

public interface JwtRegistry {

  void registerJwtInformation(JwtInformation jwtInformation);

  void invalidateJwtInformation(UUID userId);

  boolean hasActiveJwtInformationByUserId(UUID userId);

  boolean hasActiveJwtInformationByAccessToken(String accessToken);

  boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

  void clearExpiredJwtInformation();

  void invalidateJwtInformationByRefreshToken(String refreshToken);

}
