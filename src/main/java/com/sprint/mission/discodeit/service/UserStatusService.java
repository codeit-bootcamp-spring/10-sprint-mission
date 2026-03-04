package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto.userStatusResponse createUserStatus(UserStatusDto.userStatusCreateRequest createReq);
    UserStatusDto.userStatusResponse findById(UUID uuid);
    List<UserStatusDto.userStatusResponse> findAll();
    UserStatusDto.userStatusResponse updateUserStatus(UUID uuid, UserStatusDto.userStatusUpdateRequest updateReq);
    UserStatusDto.userStatusResponse updateUserStatusByUserId(UUID userId, UserStatusDto.userStatusUpdateRequest updateReq);
    void deleteUserStatusById(UUID uuid);
}
