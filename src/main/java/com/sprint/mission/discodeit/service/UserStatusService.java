package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    // creat
    UserStatus createUserStatus(UserStatusCreateRequest request);

    // read
    UserStatus findUserStatusById(UUID userStatusId);

    // all read
    List<UserStatus> findAllUserStatus();

    // update
    UserStatus updateUserStatus(UserStatusUpdateRequest request);
    UserStatus updateUserStatusByUserId(UUID userId, Instant lastOnlineTime);

    // delete
    void deleteUserStatus(UUID userStatusId);
}
