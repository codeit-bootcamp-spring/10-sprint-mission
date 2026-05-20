package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto find(UUID userStatusId);

  List<UserStatusDto> findAll();

  void delete(UUID userStatusId);
}
