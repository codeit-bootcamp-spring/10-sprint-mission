package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatus create(UUID userId);

  UserStatus findById(UUID id);

  List<UserStatus> findAll();

  UserStatus updateByUserId(UUID id, Instant newLastActiveAt);

  void deleteById(UUID id);
}
