package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.jwt.JwtInformation;
import java.util.UUID;

public interface AuthService {

  void expireUserSessions(UUID userId);

  boolean isUserLoggedIn(UUID userId);

  JwtInformation rotateToken(String oldRefreshToken);
}
