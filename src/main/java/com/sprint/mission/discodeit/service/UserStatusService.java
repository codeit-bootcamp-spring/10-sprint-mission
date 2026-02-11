package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(CreateUserStatusRequestDto request);
    UserStatusDto find(UUID userStatusId);
    List<UserStatusDto> findAll();
    UserStatusDto update(UUID userStatusId, UpdateUserStatusRequestDto request);
    UserStatusDto updateByUserId(UUID userId);
    void delete(UUID userStatusId);
}
