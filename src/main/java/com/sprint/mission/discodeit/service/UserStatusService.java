package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    UUID createUserStatus(CreateUserStatusRequest request);

    UserStatus findUserStatusByUserStatusId(UUID userStatusId);

    List<UserStatus> findAllUserStatus();

    UserStatus updateUserStatusByUserId(UUID userId, UserStatusUpdateRequest request);

    void deleteUserStatus(UUID userStatusId);
}
