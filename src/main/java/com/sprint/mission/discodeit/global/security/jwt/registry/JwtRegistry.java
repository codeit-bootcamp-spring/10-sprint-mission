package com.sprint.mission.discodeit.global.security.jwt.registry;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import java.util.UUID;

public interface JwtRegistry {

  void register(RefreshToken refreshToken);

  RefreshToken findByToken(String refreshToken);

  void invalidateAllByUserId(UUID userId);

  void invalidateByToken(String refreshToken);

  void clearExpiredTokens();

  boolean isActiveSession(UUID userId, String device);
}
