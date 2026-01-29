package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus find(UUID userID);
    UserStatus findByUserID(UUID userID);
    List<UserStatus> findAll();
    void deleteUserStatus(UUID userStatusID);
    UserStatus save(UserStatus userStatus);

}
