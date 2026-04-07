package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.user.dto.UserStatusDto;
import com.sprint.mission.discodeit.user.dto.UserStatusCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusDto create(UserStatusCreateRequest request);

  UserStatusDto find(UUID userStatusID);

  List<UserStatusDto> findAll();

  UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);

  UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
}
