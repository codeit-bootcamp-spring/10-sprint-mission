package com.sprint.mission.discodeit.registry;

import com.sprint.mission.discodeit.entity.JwtInformation;
import java.util.UUID;

public interface JwtRegistry {

  public void registerJwtInformation(JwtInformation jwtInformation);

  public void invalidateJwtInformationByUserId(UUID userId);

  public boolean hasActiveJwtInformationByUserId(UUID userId);

  public boolean hasActiveJwtInformationByAccessToken(String accessToken);

  public boolean hasActiveJwtInformationByRefreshToken(String refreshToken);

  public void rotateJwtInformation(String refreshToken, JwtInformation newJwtInformation);

  public void clearExpiredJwtInformation();
}
