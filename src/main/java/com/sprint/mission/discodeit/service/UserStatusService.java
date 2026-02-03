package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto.response createUserStatus(UserStatusDto.createRequest createReq);
    UserStatusDto.response findById(UUID uuid);
    List<UserStatusDto.response> findAll();
    UserStatusDto.response updateUserStatus(UUID uuid, UserStatusDto.updateRequest updateReq);
    UserStatusDto.response updateUserStatusByUserId(UUID userId, UserStatusDto.updateRequest updateReq);
    void deleteUserStatusById(UUID uuid);
}
