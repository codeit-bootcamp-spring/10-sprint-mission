// UserStatusService.java
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UUID create(UUID userId);

  UserStatusResponse find(UUID id);

  List<UserStatusResponse> findAll();

  UserStatusResponse update(UUID id, Instant newLastActiveAt);

  UserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt);

  void deleteByUserId(UUID userId);
}