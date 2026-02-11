package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UUID createUserStatus(CreateUserStatusRequest request);

    UserStatusResponse findUserStatusByUserStatusId(UUID userStatusId);

    List<UserStatusResponse> findAllUserStatus();

    UserStatusResponse updateUserStatusByUserId(UUID userId);

    void deleteUserStatus(UUID userStatusId);
}
