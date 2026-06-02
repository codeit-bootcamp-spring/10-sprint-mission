package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.auth.jwt.JwtInformation;
import java.util.UUID;

public interface AuthService {
  void expireUserSessions(UUID userId);

  JwtInformation refreshToken(String refreshToken);

}
