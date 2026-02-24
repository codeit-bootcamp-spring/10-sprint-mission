package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatusResponse create(UserStatusCreateRequest createRequest);

  UserStatusResponse findById(UUID userStatusId);

  List<UserStatusResponse> findAll();

  UserStatusResponse update(UUID id, UserStatusUpdateRequest updateRequest);

  UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request);

  void delete(UUID userStatusId);
}
