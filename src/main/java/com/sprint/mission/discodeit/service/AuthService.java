package com.sprint.mission.discodeit.service;

import java.util.UUID;

public interface AuthService {

  void expireUserSessions(UUID userId);

  boolean isUserOnline(UUID userId);
}
