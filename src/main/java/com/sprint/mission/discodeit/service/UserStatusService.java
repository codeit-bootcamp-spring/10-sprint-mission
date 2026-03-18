package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto createUserStatus(UserStatusDto.UserStatusCreateRequest createReq);
    UserStatusDto findById(UUID uuid);
    List<UserStatusDto> findAll();
    UserStatusDto updateUserStatus(UUID uuid, UserStatusDto.UserStatusUpdateRequest updateReq);
    UserStatusDto updateUserStatusByUserId(UUID userId, UserStatusDto.UserStatusUpdateRequest updateReq);
    void deleteUserStatusById(UUID uuid);
}
