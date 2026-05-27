package com.sprint.mission.discodeit.auth.service;

import java.util.UUID;

public interface AuthService {

  void expireUserSession(UUID userId);

}
