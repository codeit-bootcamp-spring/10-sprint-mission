package com.sprint.mission.discodeit.global.security.jwt.registry;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "discodeit.jwt.registry", value = "store-type", havingValue = "db")
@RequiredArgsConstructor
public class JpaJwtRegistry implements JwtRegistry {

  @Override
  public void register(RefreshToken refreshToken) {

  }

  @Override
  public RefreshToken findByToken(String refreshToken) {
    return null;
  }

  @Override
  public void invalidateAllByUserId(UUID userId) {

  }

  @Override
  public void invalidateByToken(String refreshToken) {

  }

  @Override
  public void clearExpiredTokens() {

  }

  @Override
  public boolean isActiveSession(UUID userId, String device) {
    return false;
  }
}
