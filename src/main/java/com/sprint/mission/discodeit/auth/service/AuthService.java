package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.jwt.JwtDto;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

public interface AuthService {

  //  void expireUserSession(UUID userId);
  JwtDto refresh(String refreshToken, HttpServletResponse response);
}
