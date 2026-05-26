package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.AuthTokenResult;

public interface AuthService {

  // 리프레시 토큰을 받아 새로운 토큰 쌍과 유저 정보를 반환
  AuthTokenResult refresh(String refreshToken);
}
