package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.jwt.JwtRegistry;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPresenceService {

  private final JwtRegistry jwtRegistry;

  public boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
