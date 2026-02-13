package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(UserStatusCreateRequest userStatusCreateRequest);
    UserStatusDto find(UUID userStatusId);
    List<UserStatusDto> findAll();
    UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest userStatusUpdateRequest);
    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest userStatusUpdateRequest);
    void delete(UUID userStatusId);
}
