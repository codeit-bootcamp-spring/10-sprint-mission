package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.JwtInformation;

public interface AuthService {

  JwtInformation refreshToken(String refreshToken);
}
