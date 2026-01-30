package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusDto.CreateRequest request);
    UserStatus find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus update(UUID userStatusId, UserStatusDto.UpdateRequest request);
    UserStatus updateByUserId(UUID userId, UserStatusDto.UpdateRequest request);

    void delete(UUID userStatusId);
}
