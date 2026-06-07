package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.JwtRefreshResultDto;

public interface AuthService {

    JwtRefreshResultDto refresh(String refreshToken);
}
