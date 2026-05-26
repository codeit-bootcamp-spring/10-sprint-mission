package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.JwtDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    JwtDto refresh(String refreshToken, HttpServletResponse response);
}
