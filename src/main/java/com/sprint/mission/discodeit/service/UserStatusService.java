package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateDto userStatusCreateDto);
    UserStatus update(UUID id, UserStatusUpdateDto userStatusUpdateDto);
    UserStatus updateByUserId(UUID userId,UserStatusUpdateDto userStatusUpdateDto);
    UserStatus find(UUID id);
    List<UserStatus> findAll();
    void delete(UUID id);
}
