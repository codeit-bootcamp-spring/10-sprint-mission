package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UserStatusDto.UserStatusResponse create(UserStatusDto.UserStatusRequest request);
    UserStatusDto.UserStatusResponse findById(UUID userStatusId);
    List<UserStatusDto.UserStatusResponse> findAll();
    UserStatusDto.UserStatusResponse update(UUID userStatusId, UserStatus.Status status);
    void delete(UUID userStatusId);
}
